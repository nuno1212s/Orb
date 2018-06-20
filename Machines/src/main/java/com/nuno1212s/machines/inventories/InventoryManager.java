package com.nuno1212s.machines.inventories;

import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.machines.machinemanager.Machine;
import com.nuno1212s.machines.main.Main;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.io.File;

public class InventoryManager {

    @Getter
    private MachineInventory mainInventory;

    @Getter
    private ConfirmInventory confirmInventory;

    public InventoryManager() {

        File dataFolder = new File(Main.getIns().getDataFolder() + File.separator + "storeInventories" + File.separator);

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();

            return;
        }

        for (File f : dataFolder.listFiles()) {

            new MachineInventory(f);

        }

        File jsonFile = new File(Main.getIns().getDataFolder(), "mainInventory.json"),
                confirmFile = new File(Main.getIns().getDataFolder(), "confirmInventory.json");

        if (!jsonFile.exists()) {

            Main.getIns().saveResource(jsonFile, "mainInventory.json");

        }

        if (!confirmFile.exists()) {
            Main.getIns().saveResource(confirmFile, "confirmInventory.json");
        }

        this.mainInventory = new MachineInventory(jsonFile);
        this.confirmInventory = new ConfirmInventory(confirmFile);

        Bukkit.getServer().getPluginManager().registerEvents(this.confirmInventory, BukkitMain.getIns());
    }

    public Inventory getInventoryForMachine(Machine m) {

        InventoryData machineInventory = MainData.getIns().getInventoryManager().getInventory("machineInventory");

        if (machineInventory == null) {
            System.out.println("Please create a machine inventory");

            return null;
        }

        return ((MachineInventory) machineInventory).buildInventory(m);
    }

}
