package com.nuno1212s.hub.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * Handles player level changes
 */
public class PlayerFoodChangeListener implements Listener {

    @EventHandler
    public void onPlayerFoodChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

}
