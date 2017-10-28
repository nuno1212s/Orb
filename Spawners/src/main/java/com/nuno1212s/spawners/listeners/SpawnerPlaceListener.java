package com.nuno1212s.spawners.listeners;

import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class SpawnerPlaceListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        if (e.getBlock().getState() instanceof CreatureSpawner) {
            //Reduce the delay of the mob spawning
            ((CreatureSpawner) e.getBlock().getState()).setDelay(1);
            e.getBlock().getState().update();
        }
    }

}
