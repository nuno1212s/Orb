package com.nuno1212s.util.inventories;

import com.nuno1212s.util.NBTDataStorage.ReflectionManager;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles inventory listeners data
 */
@SuppressWarnings("unchecked")
@ToString
public class InventoryData {

    @Getter
    protected String inventoryName;

    @Getter
    protected int inventorySize;

    @Getter
    protected List<InventoryItem> items;

    /**
     * Use the {@link #InventoryData(File, Class)} instead, this constructor is just being kept for compatibility reasons
     *
     * @param jsonFile
     */
    @Deprecated
    public InventoryData(File jsonFile) {
        this(jsonFile, InventoryItem.class);
    }

    public InventoryData(JSONObject obj) {
        load(obj, false, InventoryItem.class);
    }

    /**
     * Main constructor for the inventory data
     *
     * @param jsonFile  The file of the inventory
     * @param itemClass An optional class for the items stored in the inventory (If you want to change the
     *                  items that the inventory stores, there is no longer a need to create a subclass of InventoryData)
     */
    public InventoryData(File jsonFile, Class<? extends InventoryItem> itemClass) {
        JSONObject jsOB;

        try (Reader r = new FileReader(jsonFile)) {
            jsOB = (JSONObject) new JSONParser().parse(r);
        } catch (IOException | ParseException e) {
            System.out.println("Failed to read the JSON File " + jsonFile.getName());
            return;
        }

        load(jsOB, true, itemClass == null ? InventoryItem.class : itemClass);
    }

    /**
     * Loads the actual inventory data from the JSONObject
     *
     * @param jsOB         The object to load from
     * @param loadItems    Should the items be loaded
     * @param customLoader The loader that should be used for the items
     */
    private void load(JSONObject jsOB, boolean loadItems, Class<? extends InventoryItem> customLoader) {
        this.inventoryName = ChatColor.translateAlternateColorCodes('&',
                (String) jsOB.getOrDefault("InventoryName", "&cFailed to load name"));

        this.inventorySize = ((Long) jsOB.getOrDefault("InventorySize", 27)).intValue();

        if (inventorySize % 9 != 0) {
            this.inventorySize = 27;
        }

        if (loadItems) {
            JSONArray inventoryItems = (JSONArray) jsOB.getOrDefault("InventoryItems", new JSONArray());

            this.items = new ArrayList<>(inventoryItems.size());
            Constructor constructor = ReflectionManager.getIns().getConstructor(customLoader, JSONObject.class);

            inventoryItems.forEach((inventoryItem) -> {
                        InventoryItem e = (InventoryItem) ReflectionManager.getIns()
                                .invokeConstructor(constructor, (JSONObject) inventoryItem);
                        this.items.add(e);
                    }
            );
        }
    }

    /**
     * Build the inventory with the specified features
     *
     * @return
     */
    public Inventory buildInventory() {
        String inventoryName = this.inventoryName;

        Inventory inventory = Bukkit.getServer().createInventory(null, this.inventorySize, inventoryName);

        this.items.forEach(item -> {
            ItemStack item1 = item.getItem();
            if (item1 == null) {
                inventory.setItem(item.getSlot(), null);
                return;
            }
            inventory.setItem(item.getSlot(), item1.clone());
        });

        return inventory;
    }

    public InventoryItem getItem(int slot) {
        for (InventoryItem item : this.items) {
            if (item.getSlot() == slot) {
                return item;
            }
        }
        return null;
    }

    public InventoryItem getItemWithFlag(String flag) {
        for (InventoryItem item : this.items) {
            if (item.hasItemFlag(flag)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof InventoryData) {
            //Compare the size before the name because the size is much faster to compare than the name
            // and the size, like the name can't be changed
            return ((InventoryData) obj).getInventorySize() == this.getInventorySize()
                    && ((InventoryData) obj).getInventoryName().equals(this.getInventoryName());
        } else if (obj instanceof Inventory) {
            return ((Inventory) obj).getSize() == this.getInventorySize()
                    && ((Inventory) obj).getName().equals(this.getInventoryName());
        }

        return false;
    }
}
