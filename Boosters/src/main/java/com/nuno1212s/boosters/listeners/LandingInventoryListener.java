package com.nuno1212s.boosters.listeners;

import com.nuno1212s.boosters.main.Main;
import com.nuno1212s.util.inventories.InventoryData;
import com.nuno1212s.util.inventories.InventoryItem;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/**
 * Handles inventory actions on the landing inventory
 */
public class LandingInventoryListener implements Listener {

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (Main.getIns().getInventoryManager().getLandingInventory().equals(e.getInventory())) {
            e.setResult(Event.Result.DENY);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        InventoryData lI = Main.getIns().getInventoryManager().getLandingInventory();
        if (lI.equals(e.getInventory())) {
            if (e.getClick().isShiftClick()) {
                e.setResult(Event.Result.DENY);
            }
        } else {
            return;
        }

        if (lI.equals(e.getClickedInventory())) {
            e.setResult(Event.Result.DENY);

            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            InventoryItem item = lI.getItem(e.getSlot());

            if (item == null) {
                return;
            }

            if (item.hasItemFlag("OWN_BOOSTERS")) {
                e.getWhoClicked().closeInventory();
                Main.getIns().getInventoryManager().setPage(e.getWhoClicked().getUniqueId(), 1);
                e.getWhoClicked().openInventory(Main.getIns().getInventoryManager().buildInventoryForPlayer(e.getWhoClicked().getUniqueId(), 1));
            } else if (item.hasItemFlag("BUY_BOOSTERS")) {
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().openInventory(Main.getIns().getInventoryManager().buildStoreInventory());
            }

        }

    }

}
