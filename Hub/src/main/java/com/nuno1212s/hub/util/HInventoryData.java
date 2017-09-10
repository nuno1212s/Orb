package com.nuno1212s.hub.util;

import com.nuno1212s.util.inventories.InventoryData;
import lombok.Getter;

import java.io.File;

/**
 * Inventory data for the server selector
 */
public class HInventoryData extends InventoryData {

    @Getter
    private String inventoryID;

    public HInventoryData(String inventoryID, File file) {
        super(file, HInventoryItem.class);
        this.inventoryID = inventoryID;
    }



}
