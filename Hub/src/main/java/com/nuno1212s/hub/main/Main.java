package com.nuno1212s.hub.main;


import com.nuno1212s.hub.hotbar.HotbarManager;
import com.nuno1212s.hub.listeners.InventoryClickListener;
import com.nuno1212s.hub.listeners.PlayerInteractListener;
import com.nuno1212s.hub.server_selector.ServerSelectorManager;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;

/**
 * Main class file
 */
@ModuleData(name = "Hub", version = "1.0")
public class Main extends Module {

    @Getter
    static Main ins;

    @Getter
    private HotbarManager hotbarManager;

    @Getter
    private ServerSelectorManager serverSelectorManager;

    @Override
    public void onEnable() {
        ins = this;
        hotbarManager = new HotbarManager(this);
        serverSelectorManager = new ServerSelectorManager(this);

        BukkitMain ins = BukkitMain.getIns();
        ins.getServer().getPluginManager().registerEvents(new PlayerInteractListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new InventoryClickListener(), ins);

    }

    @Override
    public void onDisable() {

    }
}
