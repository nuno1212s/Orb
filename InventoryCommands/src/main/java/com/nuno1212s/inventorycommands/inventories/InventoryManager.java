package com.nuno1212s.inventorycommands.inventories;

import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventories.InventoryItem;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InventoryManager {

    private File inventoryFolder;

    private List<InventoryData> inventories;

    public InventoryManager(Module module) {
        this.inventoryFolder = new File(module.getDataFolder() + File.separator + "Inventories" + File.separator);

        if (!this.inventoryFolder.exists()) {
            this.inventoryFolder.mkdirs();
        }

        load();
    }

    public void load() {
        inventories = new ArrayList<>();

        for (File file : this.inventoryFolder.listFiles()) {
            inventories.add(new InventoryData<InventoryItem>(file, InventoryItem.class, true));
        }
    }

    public void reload() {
        inventories.forEach(MainData.getIns().getInventoryManager()::removeInventory);

        load();
    }

}
