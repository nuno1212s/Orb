package com.nuno1212s.mercadonegro.inventories;

import com.nuno1212s.mercadonegro.main.Main;
import com.nuno1212s.util.Pair;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Custom inventory class
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

    public Inventory getInventory(boolean hasNextPage, boolean hasPreviousPage) {
        Inventory i = Bukkit.getServer().createInventory(null, this.size, this.inventoryName);

        for (int slot = 0; slot < this.items.length; slot++) {
            if (items[slot] != null) {
                i.setItem(slot, items[slot].buildDisplayItem());
            }
        }

        if (hasNextPage) {
            Pair<Integer, ItemStack> nextPageItem = Main.getIns().getInventoryManager().getNextPageItem();
            i.setItem(nextPageItem.getKey(), nextPageItem.getValue());
        }

        if (hasPreviousPage) {
            Pair<Integer, ItemStack> previousPageItem = Main.getIns().getInventoryManager().getPreviousPageItem();
            i.setItem(previousPageItem.getKey(), previousPageItem.getValue());
        }

        return i;
    }

}
