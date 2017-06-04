package com.nuno1212s.mercado.main;

import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.mercado.database.MySql;
import com.nuno1212s.mercado.listeners.*;
import com.nuno1212s.mercado.marketmanager.MarketManager;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;

/**
 * Main module class
 */
@ModuleData(name = "Market", version = "0.1 SNAPSHOT", dependencies = {})
public class Main extends Module {

    @Getter
    private static Main ins;

    @Getter
    private MySql mySql;

    @Getter
    private MarketManager marketManager;

    @Override
    public void onEnable() {
        ins = this;
        mySql = new MySql();
        marketManager = new MarketManager(this);
        BukkitMain ins = BukkitMain.getIns();
        ins.getServer().getPluginManager().registerEvents(new LandingInventoryListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new BuyingInventoryListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new ConfirmInventoryListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new ChatListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new SellInventoryListener(), ins);
    }

    @Override
    public void onDisable() {

    }

}
