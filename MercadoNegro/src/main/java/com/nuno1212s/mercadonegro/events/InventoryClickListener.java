package com.nuno1212s.mercadonegro.events;

import com.nuno1212s.main.MainData;
import com.nuno1212s.mercadonegro.inventories.CInventory;
import com.nuno1212s.mercadonegro.inventories.InventoryManager;
import com.nuno1212s.mercadonegro.inventories.Item;
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
        if (Main.getIns().getInventoryManager().isInventory(e.getInventory().getName())) {
            e.setResult(Event.Result.DENY);
        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent e) {
        if (Main.getIns().getInventoryManager().isInventory(e.getInventory().getName())) {
            if (e.getClick().isShiftClick()) {
                e.setResult(Event.Result.DENY);
            }
        } else {
            return;
        }

        if (e.getClickedInventory() != null && e.getClickedInventory().getName().equalsIgnoreCase(e.getInventory().getName())) {
            if (e.getCurrentItem() == null) {
                return;
            }

            e.setResult(Event.Result.DENY);

            CInventory inventory = Main.getIns().getInventoryManager().getInventory(e.getClickedInventory().getName());

            if (inventory == null) {
                return;
            }

            InventoryManager iM = Main.getIns().getInventoryManager();
            if (iM.isNextPageSlot(e.getClickedInventory(), e.getSlot())) {
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().openInventory(iM.buildInventory(iM.getPage(inventory) + 1));
                return;
            }

            if (iM.isPreviousPageSlot(e.getClickedInventory(), e.getSlot())) {
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().openInventory(iM.buildInventory(iM.getPage(inventory) - 1));
                return;
            }

            Item item = inventory.getItem(e.getSlot());

            if (item == null) {
                return;
            }

            PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(e.getWhoClicked().getUniqueId());

            item.buy((Player) e.getWhoClicked(), playerData);

        }

    }

}
