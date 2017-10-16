package com.nuno1212s.warps.inventories;

import com.nuno1212s.warps.inventories.invdata.WInventoryData;
import com.nuno1212s.warps.inventories.invdata.WInventoryItem;
import com.nuno1212s.warps.main.Main;
import com.nuno1212s.warps.warpmanager.Warp;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * Handles inventory clicks
 */
public class InventoryClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        WInventoryData inventory = Main.getIns().getInventoryManager().getInventory(e.getInventory());
        if (inventory != null) {
            e.setResult(Event.Result.DENY);

            if (inventory.equals(e.getClickedInventory())) {
                WInventoryItem item = (WInventoryItem) inventory.getItem(e.getSlot());
                if (item.getConnectingWarp() == null) {

                    String connectingInventory = item.getConnectingInventory();

                    if (connectingInventory != null) {

                        Inventory inventory1 = Main.getIns().getInventoryManager().getInventory(connectingInventory);
                        if (inventory1 == null) {
                            return;
                        }

                        e.getWhoClicked().closeInventory();
                        e.getWhoClicked().openInventory(inventory1);
                    }

                } else {
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

}
