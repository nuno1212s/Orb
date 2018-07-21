package com.nuno1212s.warps.inventories.invdata;

import com.google.common.collect.ImmutableList;
import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.warps.main.Main;
import com.nuno1212s.warps.warpmanager.Warp;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Inventory ID
 */
public class WInventoryData extends InventoryData<WInventoryItem> {

    @Getter
    private String inventoryID;

    public WInventoryData(JSONObject object) {
        super(object);
        this.inventoryID = (String) object.get("InventoryID");

        JSONArray itemsJSON = (JSONArray) object.get("InventoryItems");

        ArrayList<WInventoryItem> items = new ArrayList<>();

        itemsJSON.forEach((inventoryItem) ->
                items.add(new WInventoryItem((JSONObject) inventoryItem))
        );

        this.items = ImmutableList.copyOf(items);
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        e.setResult(Event.Result.DENY);

        if (this.equals(e.getClickedInventory())) {
            WInventoryItem item = this.getItem(e.getSlot());

            if (item == null) {
                return;
            }

            if (item.getConnectingWarp() != null) {
                e.getWhoClicked().closeInventory();

                Warp w = Main.getIns().getWarpManager().getWarp(item.getConnectingWarp());

                if (w == null) {
                    return;
                }

                Player p = (Player) e.getWhoClicked();
                e.getWhoClicked().closeInventory();

                w.teleport(p);

            }
        }


    }

}
