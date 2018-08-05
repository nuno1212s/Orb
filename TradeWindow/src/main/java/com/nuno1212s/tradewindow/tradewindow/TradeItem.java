package com.nuno1212s.tradewindow.tradewindow;

import com.nuno1212s.inventories.InventoryItem;
import com.nuno1212s.util.ItemUtils;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TradeItem extends InventoryItem {

    public TradeItem(JSONObject data) {
        super(data);
    }

    public ItemStack getItem(long coins) {

        if (this.item == null) {
            return super.getItem();
        }

        Map<String, String> formats = new HashMap<>();

        formats.put("%coins%", String.valueOf(coins));

        return ItemUtils.formatItem(this.item.clone(), formats);
    }

}
