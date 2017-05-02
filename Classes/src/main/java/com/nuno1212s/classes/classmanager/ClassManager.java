package com.nuno1212s.classes.classmanager;

import com.nuno1212s.modulemanager.Module;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

/**
 * Manages classes
 */
public class ClassManager {

    private List<Class> classes;

    @Getter
    private File dataFile;

    public ClassManager(Module m) {
        classes = new ArrayList<>();
        dataFile = m.getFile("classes.json", false);

        JSONObject jsonObject;

        try (Reader reader = new FileReader(dataFile)) {
            jsonObject = (JSONObject) new JSONParser().parse(reader);
        } catch (IOException | ParseException e) {
            System.out.println("Could not read JSON file, maybe it's undefined?");
            return;
        }

        for (String o : (Set<String>) jsonObject.keySet()) {
            this.classes.add(new Class((Map<String, Object>)jsonObject.get(o)));
        }

    }

    public Class getClassEdit(String name) {
        for (Class aClass : this.classes) {
            if (name.startsWith(aClass.getClassName() + " Edit")) {
                return aClass;
            }
        }
        return null;
    }

    public boolean isClassDisplay(String inventoryName) {
        for (Class aClass : this.classes) {
            if (inventoryName.equalsIgnoreCase(aClass.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String itemTo64(ItemStack stack) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(stack);

            // Serialize that array

            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stack.", e);
        }
    }

    public static ItemStack itemFrom64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            try {
                return (ItemStack) dataInput.readObject();
            } finally {
                dataInput.close();
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

}
