package com.nuno1212s.boosters.inventories;

import com.nuno1212s.boosters.boosters.Booster;
import com.nuno1212s.boosters.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.inventories.InventoryData;
import com.nuno1212s.util.inventories.InventoryItem;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

/**
 * Handles confirm inventory clicks
 */
public class ConfirmInventoryListener implements Listener {

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (Main.getIns().getInventoryManager().getConfirmInventory().equals(e.getInventory())) {
            e.setResult(Event.Result.DENY);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        InventoryData confirmInventory = Main.getIns().getInventoryManager().getConfirmInventory();
        if (confirmInventory.equals(e.getInventory())) {
            if (e.getClick().isShiftClick()) {
                e.setResult(Event.Result.DENY);
            }
        } else {
            return;
        }

        if (e.getClickedInventory() == null) {
            return;
        }

        if (confirmInventory.equals(e.getClickedInventory())) {
            if (e.getCurrentItem() == null) {
                return;
            }

            e.setResult(Event.Result.DENY);

            InventoryItem item = confirmInventory.getItem(e.getSlot());

            if (item == null) {
                return;
            }

            InventoryItem boosterItem = confirmInventory.getItemWithFlag("BoosterItem");

            if (boosterItem == null) {
                return;
            }

            Booster boosterConnectedToItem = Main.getIns().getInventoryManager().getBoosterConnectedToItem(e.getClickedInventory().getItem(boosterItem.getSlot()));

            if (boosterConnectedToItem == null) {
                return;
            }

            if (item.hasItemFlag("CONFIRM")) {
                boosterConnectedToItem.activate();

                MainData.getIns().getMessageManager().getMessage("ACTIVATED_BOOSTER")
                        .format("%boosterName%", boosterConnectedToItem.getCustomName()).sendTo(e.getWhoClicked());

                e.getWhoClicked().closeInventory();
                int page = Main.getIns().getInventoryManager().getPage(e.getWhoClicked().getUniqueId());
                Inventory inventory = Main.getIns().getInventoryManager().buildInventoryForPlayer(e.getWhoClicked().getUniqueId(), page);
                e.getWhoClicked().openInventory(inventory);
            } else if (item.hasItemFlag("CANCEL")) {
                e.getWhoClicked().closeInventory();
                int page = Main.getIns().getInventoryManager().getPage(e.getWhoClicked().getUniqueId());
                Inventory inventory = Main.getIns().getInventoryManager().buildInventoryForPlayer(e.getWhoClicked().getUniqueId(), page);
                e.getWhoClicked().openInventory(inventory);
            }

        }

    }


}
