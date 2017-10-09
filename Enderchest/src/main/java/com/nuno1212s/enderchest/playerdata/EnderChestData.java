package com.nuno1212s.enderchest.playerdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nuno1212s.util.typeadapters.ItemStackTypeAdapter;
import org.bukkit.inventory.ItemStack;

public interface EnderChestData {

    Gson gson = new GsonBuilder().registerTypeAdapter(ItemStack.class, new ItemStackTypeAdapter()).create();

    static String inventoryToJSON(ItemStack[] items) {
        return gson.toJson(items);
    }

    static ItemStack[] inventoryFromJSON(String json) {
        return gson.fromJson(json, ItemStack[].class);
    }

    static ItemStack[] expandInventory(ItemStack[] originalItems, int newSize) {
        ItemStack[] items = new ItemStack[newSize];

        System.arraycopy(originalItems, 0, items, 0, originalItems.length);

        return items;
    }

    void updateEnderChestData(ItemStack[] items);

    ItemStack[] getItems();

}
