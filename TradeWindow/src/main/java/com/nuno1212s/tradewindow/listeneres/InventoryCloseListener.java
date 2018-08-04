package com.nuno1212s.tradewindow.listeneres;

import com.nuno1212s.tradewindow.TradeMain;
import com.nuno1212s.tradewindow.trades.Trade;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseListener implements Listener {


    @EventHandler
    public void onClose(InventoryCloseEvent e) {

        if (TradeMain.getIns().getTradeManager().getCloseExceptions().contains(e.getPlayer().getUniqueId())) {

            TradeMain.getIns().getTradeManager().getCloseExceptions().remove(e.getPlayer().getUniqueId());

        } else {
            Trade trade = TradeMain.getIns().getTradeManager().getTrade(e.getPlayer().getUniqueId());

            if (trade != null)
                TradeMain.getIns().getTradeManager().destroyTrade(trade);
        }

    }

}
