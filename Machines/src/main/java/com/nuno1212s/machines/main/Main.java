package com.nuno1212s.machines.main;

import com.nuno1212s.machines.inventories.InventoryManager;
import com.nuno1212s.machines.listeners.MPlayerJoinListener;
import com.nuno1212s.machines.machinemanager.MachineManager;
import com.nuno1212s.machines.timers.MachineTimer;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import lombok.Getter;
import org.bukkit.Bukkit;

public class Main extends Module {

    @Getter
    static Main ins;

    @Getter
    MachineManager machineManager;

    @Getter
    InventoryManager inventoryManager;

    @Override
    public void onEnable() {
        ins = this;

        machineManager = new MachineManager();
        inventoryManager = new InventoryManager();

        MainData.getIns().getScheduler().runTaskTimerAsync(new MachineTimer(), 10, 1);

        Bukkit.getServer().getPluginManager().registerEvents(new MPlayerJoinListener(), BukkitMain.getIns());

    }

    @Override
    public void onDisable() {
        machineManager.save();
    }
}
