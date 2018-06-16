package com.nuno1212s.machines.inventories;

import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.machines.main.Main;

import java.io.File;

public class InventoryManager {

    private InventoryData mainInventory;
    
    public InventoryManager() {

        File dataFolder = new File(Main.getIns().getDataFolder() + File.separator + "storeInventories" + File.separator);

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();

            return;
        }

        for (File f : dataFolder.listFiles()) {

            new MachineInventory(f);

        }

        File jsonFile = new File(Main.getIns().getDataFolder(), "mainInventory.json");

        if (!jsonFile.exists()) {

            Main.getIns().saveResource(jsonFile, "mainInventory.json");

        }

        this.mainInventory = new InventoryData(jsonFile);

    }



}
