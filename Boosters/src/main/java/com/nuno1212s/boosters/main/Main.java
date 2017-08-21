package com.nuno1212s.boosters.main;

import com.nuno1212s.boosters.boosters.BoosterManager;
import com.nuno1212s.boosters.inventories.ConfirmInventoryListener;
import com.nuno1212s.boosters.inventories.InventoryListener;
import com.nuno1212s.boosters.inventories.InventoryManager;
import com.nuno1212s.boosters.listeners.PlayerLoginListener;
import com.nuno1212s.boosters.mysql.MySql;
import com.nuno1212s.boosters.redis.RedisListener;
import com.nuno1212s.boosters.timers.BoosterTimer;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import com.nuno1212s.rediscommunication.RedisReceiver;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles main classes
 */
@ModuleData(name = "Boosters", version = "1.0", dependencies = {})
public class Main extends Module {

    @Getter
    static Main ins;

    @Getter
    BoosterManager boosterManager;

    @Getter
    RedisListener redisHandler;

    @Getter
    InventoryManager inventoryManager;

    @Getter
    public MySql mysqlHandler;

    @Override
    public void onEnable() {
        ins = this;
        mysqlHandler = new MySql();
        boosterManager = new BoosterManager();
        inventoryManager = new InventoryManager(this);
        new BoosterTimer();

        BukkitMain ins = BukkitMain.getIns();
        ins.getServer().getPluginManager().registerEvents(new PlayerLoginListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new InventoryListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new ConfirmInventoryListener(), ins);

    }

    @Override
    public void onDisable() {

    }

}
