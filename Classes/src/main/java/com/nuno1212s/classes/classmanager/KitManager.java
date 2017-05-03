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
public class KitManager {

    private List<Kit> kits;

    @Getter
    private File dataFile;

    public KitManager(Module m) {
        kits = new ArrayList<>();
        dataFile = m.getFile("classes.json", false);

        JSONObject jsonObject;

        try (Reader reader = new FileReader(dataFile)) {
            jsonObject = (JSONObject) new JSONParser().parse(reader);
        } catch (IOException | ParseException e) {
            System.out.println("Could not read JSON file, maybe it's undefined?");
            return;
        }

        for (String o : (Set<String>) jsonObject.keySet()) {
            this.kits.add(new Kit((Map<String, Object>)jsonObject.get(o)));
        }

    }

    public void save() {
        JSONObject jsonObject = new JSONObject();

        this.kits.forEach(kit ->
            jsonObject.put(kit.getClassName(), kit.save())
        );

        try (Writer w = new FileWriter(this.dataFile)) {
            jsonObject.writeJSONString(w);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Get the class for the name provided
     *
     * @param kitName The name of the kit
     * @return
     */
    public Kit getKit(String kitName) {
        for (Kit aKit : this.kits) {
            if (kitName.equalsIgnoreCase(aKit.getClassName())) {
                return aKit;
            }
        }
        return null;
    }

    /**
     * Get the class for the id provided
     *
     * @param kitID The ID of the kit
     * @return
     */
    public Kit getKit(int kitID) {
        for (Kit aKit : this.kits) {
            if (aKit.getId() == kitID) {
                return aKit;
            }
        }
        return null;
    }

    /**
     * Get the class for the editing inventory
     *
     * @param name The inventory name
     * @return the kit that matches it
     */
    public Kit getKitEdit(String name) {
        for (Kit aKit : this.kits) {
            if (name.startsWith(aKit.getClassName() + " Edit")) {
                return aKit;
            }
        }
        return null;
    }

    public boolean isKitDisplay(String inventoryName) {
        for (Kit aKit : this.kits) {
            if (inventoryName.equalsIgnoreCase(aKit.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void addKit(Kit k) {
        this.kits.add(k);
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
