package com.nuno1212s.classes.events;

import com.nuno1212s.classes.Main;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Handles clicking in class inventories
 */
public class ClassDisplayInventoryClickListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {
        if (Main.getIns().getKitManager().isKitDisplay(e.getInventory().getName())) {
            e.setResult(Event.Result.DENY);
        }
    }

}
