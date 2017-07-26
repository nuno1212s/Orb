package com.nuno1212s.warps.inventories.invdata;

import com.nuno1212s.util.inventories.InventoryData;
import lombok.Getter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Inventory ID
 */
public class WInventoryData extends InventoryData {

    @Getter
    private String inventoryID;

    public WInventoryData(JSONObject object) {
        super(object);
        this.inventoryID = (String) object.get("InventoryID");

        JSONArray items = (JSONArray) object.get("InventoryItems");


        items.forEach((inventoryItem) ->
                this.items.add(new WInventoryItem((JSONObject) inventoryItem))
        );
    }

}
