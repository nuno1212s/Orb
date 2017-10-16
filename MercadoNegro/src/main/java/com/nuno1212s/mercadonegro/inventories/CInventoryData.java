package com.nuno1212s.mercadonegro.inventories;

import com.nuno1212s.util.inventories.InventoryData;
import com.nuno1212s.util.inventories.InventoryItem;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.io.File;

public class CInventoryData extends InventoryData {

    public CInventoryData(File inventoryFile) {
        super(inventoryFile, CInventoryItem.class);
    }

    @Override
    public Inventory buildInventory() {
        Inventory i = Bukkit.getServer().createInventory(null, getInventorySize(), getInventoryID());

        for (InventoryItem inventoryItem : this.getItems()) {
            if (inventoryItem instanceof CInventoryItem) {
                i.setItem(inventoryItem.getSlot(), ((CInventoryItem) inventoryItem).getDisplayItem());
            }
        }

        return i;
    }
}
