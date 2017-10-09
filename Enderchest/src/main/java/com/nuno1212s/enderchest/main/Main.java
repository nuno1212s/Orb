package com.nuno1212s.enderchest.main;

import com.nuno1212s.enderchest.commands.EnderChestCommand;
import com.nuno1212s.enderchest.enderchestmanager.EnderChestManager;
import com.nuno1212s.enderchest.listeners.InventoryCloseListener;
import com.nuno1212s.enderchest.listeners.PlayerInteractListener;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;

@ModuleData(name = "EnderChest", version = "1.0")
public class Main extends Module {

    @Getter
    static Main ins;

    @Getter
    private EnderChestManager enderChestManager;

    @Override
    public void onEnable() {
        ins = this;
        enderChestManager = new EnderChestManager(this);

        registerCommand(new String[]{"enderchest"}, new EnderChestCommand());

        BukkitMain ins = BukkitMain.getIns();

        ins.getServer().getPluginManager().registerEvents(new PlayerInteractListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new InventoryCloseListener(), ins);
    }

    @Override
    public void onDisable() {

    }
}
