package com.nuno1212s.classes.classmanager;

import com.nuno1212s.modulemanager.Module;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
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
    private File dataFile, inventoryFile;

    @Getter
    private DisplayInventory displayInventory;

    public KitManager(Module m) {
        kits = new ArrayList<>();
        dataFile = m.getFile("classes.json", false);
        inventoryFile = m.getFile("displayinventory.json", true);
    }

    public void load() {
        JSONObject dataFile, displayData;

        try (Reader reader = new FileReader(this.dataFile);
             Reader reader2 = new FileReader(this.inventoryFile)) {
            JSONParser jsonParser = new JSONParser();
            dataFile = (JSONObject) jsonParser.parse(reader);
            displayData = (JSONObject) jsonParser.parse(reader2);
        } catch (IOException | ParseException e) {
            System.out.println("Could not read JSON file, maybe it's undefined?");
            return;
        }

        for (String o : (Set<String>) dataFile.keySet()) {
            this.kits.add(new Kit((Map<String, Object>)dataFile.get(o)));
        }

        displayInventory = new DisplayInventory(displayData, this);

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

    /**
     * Is the inventory a display of a kit
     *
     * @param inventoryName The name of the inventory to check
     * @return
     */
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

    /**
     * Removes the kit from the kit list
     *
     * @param k The kit to remove
     */
    public void removeKit(Kit k) {
        this.kits.remove(k);
    }

    public Inventory buildInventory() {

        return null;
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

class DisplayInventory {

    private String inventoryName;

    private Kit[] items;

    public DisplayInventory(JSONObject displayData, KitManager manager) {
        int inventorySize = ((Long) displayData.get("InventorySize")).intValue();
        this.inventoryName = ChatColor.translateAlternateColorCodes('&', (String) displayData.get("InventoryName"));
        this.items = new Kit[inventorySize];
        Map<String, Object> items1 = (Map<String, Object>) displayData.get("Items");

        items1.forEach((slot, kitID) -> {
            int iSlot = Integer.parseInt(slot);
            int iKitID = ((Long) kitID).intValue();
            items[iSlot] = manager.getKit(iKitID);
        });
    }

    public Inventory getInventory() {
        Inventory i = Bukkit.getServer().createInventory(null, items.length, inventoryName);
        for (int i2 = 0; i2 < items.length; i2++) {
            Kit item = items[i2];
            if (item == null) {
                continue;
            }
            i.setItem(i2, item.getDisplayItem().clone());
        }
        return i;
    }

}
