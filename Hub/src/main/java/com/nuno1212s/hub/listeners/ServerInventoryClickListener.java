package com.nuno1212s.hub.listeners;

import com.nuno1212s.hub.main.Main;
import com.nuno1212s.hub.util.HInventoryItem;
import com.nuno1212s.util.inventories.InventoryData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/**
 * Click listener
 */
public class ServerInventoryClickListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onDrag(InventoryDragEvent e) {
        InventoryData inventoryDataByName = Main.getIns().getServerSelectorManager().getInventoryDataByName(e.getInventory().getName());
        if (inventoryDataByName != null) {
            e.setResult(Event.Result.DENY);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent e) {
        InventoryData inventoryData = Main.getIns().getServerSelectorManager().getInventoryDataByName(e.getInventory().getName());
        if (inventoryData.equals(e.getInventory())) {
            if (e.getClick().isShiftClick()) {
                e.setResult(Event.Result.DENY);
            }
        } else {
            return;
        }

        if (inventoryData.equals(e.getClickedInventory())) {

            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

            e.setResult(Event.Result.DENY);

            HInventoryItem item = (HInventoryItem) inventoryData.getItem(e.getSlot());

            if (item == null) return;

            if (item.getConnectingInventory() != null) {
                InventoryData nextInventory = Main.getIns().getServerSelectorManager().getInventoryData(item.getConnectingInventory());

                if (nextInventory == null) {
                    return;
                }

                e.getWhoClicked().closeInventory();
                e.getWhoClicked().openInventory(nextInventory.buildInventory());
                return;
            }

            String connectingServer = item.getConnectingServer();

            if (connectingServer == null) return;

            e.getWhoClicked().closeInventory();
            item.sendPlayerToServer((Player) e.getWhoClicked());

        }

    }

}
