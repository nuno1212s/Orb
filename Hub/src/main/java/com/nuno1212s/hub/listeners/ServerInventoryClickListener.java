package com.nuno1212s.hub.listeners;

import com.nuno1212s.hub.main.Main;
import com.nuno1212s.hub.util.HInventoryData;
import com.nuno1212s.hub.util.HInventoryItem;
import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.main.MainData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

/**
 * Click listener
 */
public class ServerInventoryClickListener implements Listener {

    @EventHandler
    public void onOpen(InventoryOpenEvent e) {
        InventoryData inventoryDataByName = MainData.getIns().getInventoryManager().getInventoryByName(e.getInventory().getName());
        if (inventoryDataByName instanceof HInventoryData) {
            Main.getIns().getServerSelectorManager().getOpenInventories().put(e.getPlayer().getUniqueId(), inventoryDataByName.getInventoryID());
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        InventoryData inventoryDataByName = MainData.getIns().getInventoryManager().getInventoryByName(e.getInventory().getName());
        if (inventoryDataByName != null) {
            Main.getIns().getServerSelectorManager().getOpenInventories().remove(e.getPlayer().getUniqueId());
        }
    }

}
