package com.nuno1212s.tradewindow.tradewindow;

import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventories.InventoryItem;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.json.simple.JSONObject;

import java.io.File;

public class TradeInventory extends InventoryData<TradeItem> {

    public TradeInventory(File file) {
        super(file, TradeItem.class, true);
    }


    @Override
    public void handleClick(InventoryClickEvent e) {
        e.setResult(Event.Result.DENY);



    }
}

class TradeItem extends InventoryItem {

    public TradeItem(JSONObject data) {
        super(data);
    }
}
