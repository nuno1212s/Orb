package com.nuno1212s.hub.main;

import com.nuno1212s.hub.hotbar.HotbarManager;
import com.nuno1212s.hub.listeners.OptionsInventoryClickListener;
import com.nuno1212s.hub.listeners.PlayerInteractListener;
import com.nuno1212s.hub.listeners.PlayerJoinListener;
import com.nuno1212s.hub.listeners.ServerInventoryClickListener;
import com.nuno1212s.hub.player_options.PlayerOptionsManager;
import com.nuno1212s.hub.redis.RedisHandler;
import com.nuno1212s.hub.server_selector.ServerSelectorManager;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;

/**
 * Main class file
 */
@ModuleData(name = "Hub", version = "1.0", dependencies = {"Displays"})
public class Main extends Module {

    @Getter
    static Main ins;

    @Getter
    private HotbarManager hotbarManager;

    @Getter
    private ServerSelectorManager serverSelectorManager;

    @Getter
    private PlayerOptionsManager playerOptionsManager;

    @Getter
    private RedisHandler redisHandler;

    @Override
    public void onEnable() {
        ins = this;
        hotbarManager = new HotbarManager(this);
        serverSelectorManager = new ServerSelectorManager(this);
        playerOptionsManager = new PlayerOptionsManager(this);
        redisHandler = new RedisHandler();

        BukkitMain ins = BukkitMain.getIns();
        ins.getServer().getPluginManager().registerEvents(new PlayerInteractListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new ServerInventoryClickListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new OptionsInventoryClickListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), ins);

    }

    @Override
    public void onDisable() {

    }
}
