package com.nuno1212s.punishments.listeners;

import com.nuno1212s.punishments.main.Main;
import com.nuno1212s.punishments.util.PInventoryItem;
import com.nuno1212s.util.inventories.InventoryData;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        InventoryData inventoryFromInventory = Main.getIns().getInventoryManager().getInventoryFromInventory(e.getClickedInventory());
        if (inventoryFromInventory != null) {
            if (!e.getClick().isShiftClick()) {
                e.setResult(Event.Result.DENY);
            }
        } else {
            return;
        }

        if (inventoryFromInventory.equals(e.getClickedInventory())) {
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

            e.setResult(Event.Result.DENY);

            PInventoryItem item = (PInventoryItem) inventoryFromInventory.getItem(e.getSlot());

            if (item == null) {
                return;
            }

            if (item.getConnectingInventory() != null) {
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().openInventory(Main.getIns().getInventoryManager().getInventory(item.getConnectingInventory()));
                return;
            }

            item.applyToPlayer(Main.getIns().getInventoryManager().getTargetForPlayer(e.getWhoClicked().getUniqueId()));

        }

    }

}
