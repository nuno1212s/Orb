package com.nuno1212s.boosters.main;

import com.nuno1212s.boosters.boosters.BoosterManager;
import com.nuno1212s.boosters.commands.BoosterCommandManager;
import com.nuno1212s.boosters.commands.GiveBoosterToPlayerCommand;
import com.nuno1212s.boosters.listeners.*;
import com.nuno1212s.boosters.inventories.InventoryManager;
import com.nuno1212s.boosters.mysql.MySql;
import com.nuno1212s.boosters.redis.RedisListener;
import com.nuno1212s.boosters.timers.BoosterTimer;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;

/**
 * Handles main classes
 */
@ModuleData(name = "Boosters", version = "1.0", dependencies = {"RankMultipliers"})
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
        redisHandler = new RedisListener();
        new BoosterTimer();

        MainData.getIns().getMessageManager().addMessageFile(getFile("messages.json", true));

        registerCommand(new String[]{"givebooster"}, new GiveBoosterToPlayerCommand());
        registerCommand(new String[]{"booster", "boosters"}, new BoosterCommandManager());

        BukkitMain ins = BukkitMain.getIns();
        ins.getServer().getPluginManager().registerEvents(new PlayerLoginListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new OwnBoostersInventoryListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new ConfirmInventoryListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new PlayerDisconnectListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new LandingInventoryListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new ConfirmSellInventoryListener(), ins);

    }

    @Override
    public void onDisable() {

    }

}
