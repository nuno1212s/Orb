package com.nuno1212s.mercadonegro.events;

import com.nuno1212s.main.MainData;
import com.nuno1212s.mercadonegro.inventories.CInventoryData;
import com.nuno1212s.mercadonegro.inventories.CInventoryItem;
import com.nuno1212s.mercadonegro.inventories.InventoryManager;
import com.nuno1212s.mercadonegro.main.Main;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/**
 * Handles inventorylisteners click listener
 */
public class InventoryClickListener implements Listener {

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (Main.getIns().getInventoryManager().getInventory(e.getInventory()) != null) {
            e.setResult(Event.Result.DENY);
        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent e) {
        CInventoryData inventory1 = Main.getIns().getInventoryManager().getInventory(e.getInventory());
        if (inventory1 != null) {
            if (e.getClick().isShiftClick()) {
                e.setResult(Event.Result.DENY);
            }
        } else {
            return;
        }

        if (e.getClickedInventory() != null && inventory1.equals(e.getClickedInventory())) {
            if (e.getCurrentItem() == null) {
                return;
            }

            e.setResult(Event.Result.DENY);

            CInventoryItem item = (CInventoryItem) inventory1.getItem(e.getSlot());

            if (item == null) {
                return;
            }

            if (item.getConnectingInventory() != null) {
                e.getWhoClicked().closeInventory();

                CInventoryData nextInventory = Main.getIns().getInventoryManager().getInventory(item.getConnectingInventory());

                if (nextInventory != null) {
                    e.getWhoClicked().openInventory(nextInventory.buildInventory());
                }

                return;
            }

            PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(e.getWhoClicked().getUniqueId());

            item.buyItem(playerData);

        }

    }

}
