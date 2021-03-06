package com.nuno1212s.crates.events;

import com.nuno1212s.crates.animations.AnimationManager;
import com.nuno1212s.crates.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * Handles player close inventorylisteners events
 */
public class PlayerCloseInventoryListener implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        AnimationManager animationManager = Main.getIns().getCrateManager().getAnimationManager();
        if (animationManager.isInventoryBeingUsed(e.getInventory())) {
            animationManager.cancelAnimation((Player) e.getPlayer(), e.getInventory());
        }
    }

}
