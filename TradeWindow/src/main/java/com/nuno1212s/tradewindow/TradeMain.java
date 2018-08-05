package com.nuno1212s.tradewindow;

import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import com.nuno1212s.tradewindow.chathandlers.ChatRequests;
import com.nuno1212s.tradewindow.commands.TradeCommand;
import com.nuno1212s.tradewindow.listeneres.InventoryCloseListener;
import com.nuno1212s.tradewindow.timers.RemoveRequestsTimer;
import com.nuno1212s.tradewindow.trades.TradeManager;
import com.nuno1212s.tradewindow.tradewindow.TradeInventory;
import lombok.Getter;

import java.io.File;

@ModuleData(name = "Trade Window", version = "1.0 ALPHA")
public class TradeMain extends Module {

    @Getter
    static TradeMain ins;

    @Getter
    TradeManager tradeManager;

    @Getter
    ChatRequests chatRequests;

    @Getter
    TradeInventory tradeInventory;

    @Override
    public void onEnable() {
        ins = this;

        chatRequests = new ChatRequests();
        tradeManager = new TradeManager(this);

        File file = new File(getDataFolder(), "tradewindow.json");

        if (!file.exists()) {

            saveResource(file, "tradewindow.json");

        }

        this.tradeInventory = new TradeInventory(file);

        File messageFile = new File(getDataFolder(), "messages.json");

        if (!messageFile.exists()) {

            saveResource(messageFile, "messages.json");

        }

        MainData.getIns().getMessageManager().addMessageFile(messageFile);

        BukkitMain.getIns().getServer().getPluginManager().registerEvents(chatRequests, BukkitMain.getIns());
        BukkitMain.getIns().getServer().getPluginManager().registerEvents(new InventoryCloseListener(), BukkitMain.getIns());

        MainData.getIns().getScheduler().runTaskTimerAsync(new RemoveRequestsTimer(), 1200, 1200);

        registerCommand(new String[]{"trade"}, new TradeCommand());
    }

    @Override
    public void onDisable() {

        tradeManager.getTrades().forEach(getTradeManager()::destroyTrade);

    }
}
