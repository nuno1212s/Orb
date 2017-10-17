package com.nuno1212s.boosters.listeners;

import com.nuno1212s.boosters.main.Main;
import com.nuno1212s.util.inventories.InventoryData;
import com.nuno1212s.util.inventories.InventoryItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/**
 * Handles the confirm sell inventory
 */
public class ConfirmSellInventoryListener implements Listener {

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (Main.getIns().getInventoryManager().getConfirmSellInventory().equals(e.getInventory())) {
            e.setResult(Event.Result.DENY);
        }
    }

    @EventHandler
    public void onSell(InventoryClickEvent e) {
        InventoryData cSI = Main.getIns().getInventoryManager().getConfirmSellInventory();
        if (cSI.equals(e.getInventory())) {
            if (e.getClick().isShiftClick()) {
                e.setResult(Event.Result.DENY);
            }
        } else {
            return;
        }

        if (cSI.equals(e.getClickedInventory())) {
            e.setResult(Event.Result.DENY);

            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            InventoryItem item = cSI.getItem(e.getSlot());

            InventoryItem boosterItem = cSI.getItemWithFlag("BOOSTER");

            if (boosterItem == null) {
                return;
            }

            if (item.hasItemFlag("CONFIRM")) {
                Main.getIns().getInventoryManager().buyBooster((Player) e.getWhoClicked(),
                        e.getInventory().getItem(boosterItem.getSlot()));
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().openInventory(Main.getIns().getInventoryManager().buildLandingInventory());
            } else if (item.hasItemFlag("CANCEL")) {
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().openInventory(Main.getIns().getInventoryManager().buildStoreInventory());
            }

        }

    }

}
