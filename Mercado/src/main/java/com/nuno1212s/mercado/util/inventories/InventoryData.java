package com.nuno1212s.mercado.util.inventories;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Handles inventorylisteners data
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

    public InventoryData(File jsonFile) {
        JSONObject jsOB;

        try (Reader r = new FileReader(jsonFile)) {
            jsOB = (JSONObject) new JSONParser().parse(r);
        } catch (IOException | ParseException e) {
            System.out.println("Failed to read the JSON File " + jsonFile.getName());
            return;
        }

        load(jsOB, true);

    }

    public InventoryData(JSONObject obj) {
        load(obj, false);
    }

    void load(JSONObject jsOB, boolean loadItems) {
        this.inventoryName = ChatColor.translateAlternateColorCodes('&', (String) jsOB.get("InventoryName"));
        this.inventorySize = ((Long) jsOB.get("InventorySize")).intValue();
        if (inventorySize % 9 != 0) {
            this.inventorySize = 27;
        }

        if (loadItems) {
            JSONArray inventoryItems = (JSONArray) jsOB.get("InventoryItems");

            this.items = new ArrayList<>(inventoryItems.size());
            inventoryItems.forEach((inventoryItem) ->
                    this.items.add(new InventoryItem((JSONObject) inventoryItem))
            );
        }
    }

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
            return ((InventoryData) obj).getInventoryName().equals(this.getInventoryName());
        } else if (obj instanceof Inventory) {
            return ((Inventory) obj).getName().equals(this.getInventoryName());
        }

        return false;
    }
}
