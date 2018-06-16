package com.nuno1212s.punishments.listeners;

import com.nuno1212s.main.MainData;
import com.nuno1212s.punishments.inventories.PInventory;
import com.nuno1212s.punishments.main.Main;
import com.nuno1212s.punishments.inventories.PInventoryItem;
import com.nuno1212s.inventories.InventoryData;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        InventoryData inventoryFromInventory = MainData.getIns().getInventoryManager().getInventoryByName(e.getInventory().getName());
        if (inventoryFromInventory != null) {

            if (inventoryFromInventory instanceof PInventory) {
                if (PInventory.getNotRemove().contains(e.getPlayer().getUniqueId())) {
                    PInventory.getNotRemove().remove(e.getPlayer().getUniqueId());
                    return;
                }

                Main.getIns().getInventoryManager().removeTargetForPlayer(e.getPlayer().getUniqueId());
            }
        }
    }

}
