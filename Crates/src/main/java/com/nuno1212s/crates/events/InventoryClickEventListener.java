package com.nuno1212s.crates.events;

import com.nuno1212s.crates.Main;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Inventory click event
 */
public class InventoryClickEventListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) {
            return;
        }

        if (Main.getIns().getCrateManager().getAnimationManager().isInventoryBeingUsed(e.getInventory())) {
            e.setResult(Event.Result.DENY);
        }
    }

}
