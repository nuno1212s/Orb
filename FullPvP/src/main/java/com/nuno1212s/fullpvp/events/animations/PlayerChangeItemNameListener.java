package com.nuno1212s.fullpvp.events.animations;

import com.nuno1212s.fullpvp.main.Main;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;

/**
 * Handles players changing the key names
 */
public class PlayerChangeItemNameListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChangeItemName(InventoryClickEvent e) {
        if (e.getInventory().getType() == InventoryType.ANVIL) {
            if (e.getRawSlot() == 2) {
                AnvilInventory inventory = (AnvilInventory) e.getInventory();

                if (Main.getIns().getCrateManager().isCrateKey(inventory.getItem(0))) {
                    e.setResult(Event.Result.DENY);
                }

            }
        }
    }

}
