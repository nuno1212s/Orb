package com.nuno1212s.warps.inventories.invdata;

import com.nuno1212s.util.inventories.InventoryItem;
import lombok.Getter;
import org.json.simple.JSONObject;

/**
 * Inventory item
 */
public class WInventoryItem extends InventoryItem {

    @Getter
    String connectingWarp;

    public WInventoryItem(JSONObject jsonObject) {
        super(jsonObject);
        if (jsonObject.containsKey("ConnectingWarp")) {
            connectingWarp = (String) jsonObject.get("ConnectingWarp");
        } else {
            connectingWarp = null;
        }
    }

}
