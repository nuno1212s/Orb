package com.nuno1212s.tradewindow;

import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import com.nuno1212s.tradewindow.chathandlers.ChatRequests;
import com.nuno1212s.tradewindow.listeneres.InventoryCloseListener;
import com.nuno1212s.tradewindow.trades.TradeManager;
import com.nuno1212s.tradewindow.tradewindow.TradeInventory;
import lombok.Getter;

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
        chatRequests = new ChatRequests();
        tradeManager = new TradeManager(this);

        BukkitMain.getIns().getServer().getPluginManager().registerEvents(chatRequests, BukkitMain.getIns());
        BukkitMain.getIns().getServer().getPluginManager().registerEvents(new InventoryCloseListener(), BukkitMain.getIns());
    }

    @Override
    public void onDisable() {

    }
}
