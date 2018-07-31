package com.nuno1212s.tradewindow.tradewindow;

import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventories.InventoryItem;
import com.nuno1212s.tradewindow.TradeMain;
import com.nuno1212s.tradewindow.trades.Trade;
import com.nuno1212s.tradewindow.trades.TradeManager;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.List;

public class TradeInventory extends InventoryData<TradeItem> {

    public TradeInventory(File file) {
        super(file, TradeItem.class, true);
    }


    @Override
    public void handleClick(InventoryClickEvent e) {
        e.setResult(Event.Result.DENY);

        TradeItem item = getItem(e.getSlot());

        Trade t = TradeMain.getIns().getTradeManager().getTrade(e.getWhoClicked().getUniqueId());

        if (item != null) {

            if (item.hasItemFlag("PLAYER1_ACCEPT")) {


                if (!t.player1ToggleAccept(e.getWhoClicked().getUniqueId())) {
                    e.setResult(Event.Result.DENY);

                    return;
                }

                if (t.isPlayer1Accepted()) {
                    e.getClickedInventory().setItem(item.getSlot(), TradeMain.getIns().getTradeManager().getAcceptedItem());
                } else {
                    e.getClickedInventory().setItem(item.getSlot(), TradeMain.getIns().getTradeManager().getRejectedItem());
                }

            } else if (item.hasItemFlag("PLAYER2_ACCEPT")) {

                if (!t.player2ToggleAccept(e.getWhoClicked().getUniqueId())) {
                    e.setResult(Event.Result.DENY);

                    return;
                }

                if (t.isPlayer2Accepted()) {
                    e.getClickedInventory().setItem(item.getSlot(), TradeMain.getIns().getTradeManager().getAcceptedItem());
                } else {
                    e.getClickedInventory().setItem(item.getSlot(), TradeMain.getIns().getTradeManager().getRejectedItem());
                }
            }

            return;
        }

        t.playerChangedTrade();

        List<TradeItem> accept = getItemsWithFlag("ACCEPT");

        for (TradeItem tradeItem : accept) {
            e.getClickedInventory().setItem(tradeItem.getSlot(), TradeMain.getIns().getTradeManager().getRejectedItem());
        }

    }
}

class TradeItem extends InventoryItem {

    public TradeItem(JSONObject data) {
        super(data);
    }
}
