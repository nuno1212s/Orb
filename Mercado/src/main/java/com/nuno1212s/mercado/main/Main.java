package com.nuno1212s.mercado.main;

import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.mercado.commands.MarketCommand;
import com.nuno1212s.mercado.commands.OpenDirectBuy;
import com.nuno1212s.mercado.database.MySql;
import com.nuno1212s.mercado.listeners.*;
import com.nuno1212s.mercado.marketmanager.MarketManager;
import com.nuno1212s.mercado.redishandler.MRedisListener;
import com.nuno1212s.mercado.searchmanager.inventorylisteners.SearchInventoryListener;
import com.nuno1212s.mercado.util.RomanNumber;
import com.nuno1212s.mercado.util.TranslatableComponents;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;

/**
 * Main module class
 */
@ModuleData(name = "Market", version = "0.1 SNAPSHOT", dependencies = {"Ferreiro"})
public class Main extends Module {

    @Getter
    private static Main ins;

    @Getter
    private MySql mySql;

    @Getter
    private MarketManager marketManager;

    @Getter
    private MRedisListener redisHandler;

    @Getter
    private TranslatableComponents translations;

    @Override
    public void onEnable() {
        new RomanNumber();
        ins = this;
        mySql = new MySql();
        marketManager = new MarketManager(this);
        translations = new TranslatableComponents(this);
        MainData.getIns().getRedisHandler().registerRedisListener((redisHandler = new MRedisListener()));

        registerCommand(new String[]{"market", "mercado"}, new MarketCommand());
        registerCommand(new String[]{"opendirectbuy"}, new OpenDirectBuy());

        MainData.getIns().getMessageManager().addMessageFile(getFile("messages.json", true));

        BukkitMain ins = BukkitMain.getIns();
        ins.getServer().getPluginManager().registerEvents(new LandingInventoryListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new BuyingInventoryListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new ConfirmInventoryListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new ChatListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new SellInventoryListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new OwnInventoryListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new SearchInventoryListener(), ins);
    }

    @Override
    public void onDisable() {

    }

}
