package com.nuno1212s.classes.inventories;

import com.nuno1212s.util.inventories.InventoryData;
import com.nuno1212s.util.inventories.InventoryItem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class KInventoryData extends InventoryData {

    @Getter
    String inventoryID;

    public KInventoryData(JSONObject j) {
        super(j);

        this.inventoryID = (String) j.get("InventoryID");

        JSONArray inventoryItems = (JSONArray) j.get("InventoryItems");

        this.items = new ArrayList<>(inventoryItems.size());
        inventoryItems.forEach((inventoryItem) ->
                this.items.add(new KInventoryItem((JSONObject) inventoryItem))
        );

    }

    public Inventory buildInventory(Player p) {
        Inventory i = Bukkit.getServer().createInventory(null, this.inventorySize, this.inventoryName);

        for (InventoryItem item : this.items) {
            if (item instanceof KInventoryItem) {
                i.setItem(item.getSlot(), ((KInventoryItem) item).buildItem(p));
            } else {
                i.setItem(item.getSlot(), item.getItem().clone());
            }
        }

        return i;
    }

}
