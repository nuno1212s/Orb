package com.nuno1212s.mercadonegro.inventories;

import com.nuno1212s.util.inventories.InventoryData;
import org.bukkit.inventory.Inventory;

import java.io.File;

public class CInventoryData extends InventoryData {

    public CInventoryData(File inventoryFile) {
        super(inventoryFile);
    }

    @Override
    public Inventory buildInventory() {
        Inventory inventory = super.buildInventory();



        return inventory;
    }
}
