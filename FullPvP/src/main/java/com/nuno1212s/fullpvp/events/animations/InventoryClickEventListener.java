package com.nuno1212s.fullpvp.events.animations;

import com.nuno1212s.fullpvp.main.Main;
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
        if (Main.getIns().getCrateManager().getAnimationManager().isInventoryBeingUsed(e.getClickedInventory())) {
            e.setResult(Event.Result.DENY);
        }
    }

}
