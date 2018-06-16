package com.nuno1212s.mercado.inventories;

import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventories.InventoryItem;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;

public class MInventoryData extends InventoryData<InventoryItem> {

    public MInventoryData(File f) {
        super(f);
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        //Ignore
    }
}
