package com.nuno1212s.machines.main;

import com.nuno1212s.machines.commands.MachineCommand;
import com.nuno1212s.machines.inventories.InventoryManager;
import com.nuno1212s.machines.listeners.MPlayerJoinListener;
import com.nuno1212s.machines.listeners.MachineDestroyListener;
import com.nuno1212s.machines.listeners.MachineInteractListener;
import com.nuno1212s.machines.listeners.MachinePlaceListener;
import com.nuno1212s.machines.machinemanager.MachineManager;
import com.nuno1212s.machines.timers.MachineTimer;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.File;

@ModuleData(name = "Machines", version = "1.0")
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

        setupMessages();

        machineManager = new MachineManager();
        inventoryManager = new InventoryManager();

        registerCommand(new String[]{"maquinas"}, new MachineCommand());

        MainData.getIns().getScheduler().runTaskTimerAsync(new MachineTimer(), 10, 1);

        Bukkit.getServer().getPluginManager().registerEvents(new MPlayerJoinListener(), BukkitMain.getIns());
        Bukkit.getServer().getPluginManager().registerEvents(new MachinePlaceListener(), BukkitMain.getIns());
        Bukkit.getServer().getPluginManager().registerEvents(new MachineDestroyListener(), BukkitMain.getIns());
        Bukkit.getServer().getPluginManager().registerEvents(new MachineInteractListener(), BukkitMain.getIns());
    }

    private void setupMessages() {

        File messages = new File(this.getDataFolder(), "messages.json");

        if (!messages.exists()) {
            saveResource(messages, "messages.json");
        }

        MainData.getIns().getMessageManager().addAndLoad(messages);

    }

    @Override
    public void onDisable() {
        machineManager.shutDown();
    }
}
