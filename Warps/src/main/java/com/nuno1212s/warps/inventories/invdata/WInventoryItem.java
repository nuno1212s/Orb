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
        }
    }

    public String getConnectingInventory() {
        for (String s : this.getItemFlags()) {
            if (s.startsWith("CONNECTING_INVENTORY")) {
                return s.split("_")[1];
            }
        }
        return null;
    }

}
