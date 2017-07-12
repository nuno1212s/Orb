package com.nuno1212s.mercadonegro.inventories;

import com.nuno1212s.mercadonegro.main.Main;
import com.nuno1212s.util.Pair;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom inventorylisteners class
 */
public class CInventory {

    @Getter
    private String inventoryName;

    @Getter
    private int size;

    @Getter
    private Item[] items;

    public CInventory(String inventoryName, int size, Item[] items) {
        this.inventoryName = inventoryName;
        this.size = size;
        this.items = items;
    }

    CInventory(Map<String, Object> data) {
        this.inventoryName = (String) data.get("InventoryName");
        this.size = ((Long) data.get("Size")).intValue();
        Map<String, Object> items = (Map<String, Object>) data.get("Items");
        this.items = new Item[this.size];
        items.forEach((slot, item) -> {
            int iSlot = Integer.parseInt(slot);
            Item i = new Item((Map<String, Object>) item);
            this.items[iSlot] = i;
        });

    }

    Map<String, Object> toJSONData() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("InventoryName", this.getInventoryName());
        data.put("Size", this.getSize());
        Map<String, Object> items = new HashMap<>();
        for (int i = 0; i < this.getItems().length; i++) {
            if (this.items[i] == null) {
                continue;
            }
            items.put(String.valueOf(i), this.items[i].toJSONData());
        }
        data.put("Items", items);
        return data;
    }

    Inventory getInventory(boolean hasNextPage, boolean hasPreviousPage) {
        Inventory i = Bukkit.getServer().createInventory(null, this.size, this.inventoryName);

        for (int slot = 0; slot < this.items.length; slot++) {
            if (items[slot] != null) {
                i.setItem(slot, items[slot].buildDisplayItem());
            }
        }

        if (hasNextPage) {
            Pair<Integer, ItemStack> nextPageItem = Main.getIns().getInventoryManager().getNextPageItem();
            i.setItem((this.size - 1) - nextPageItem.getKey(), nextPageItem.getValue());
        }

        if (hasPreviousPage) {
            Pair<Integer, ItemStack> previousPageItem = Main.getIns().getInventoryManager().getPreviousPageItem();
            i.setItem((this.size - 1) - previousPageItem.getKey(), previousPageItem.getValue());
        }

        return i;
    }

    public void setItem(int slot, Item i) {
        items[slot] = i;
    }

    public Item getItem(int slot) {
        return items[slot];
    }

}
