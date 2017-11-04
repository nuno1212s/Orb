package com.nuno1212s.hub.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerAlterTerrainListener implements Listener {

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent e) {
        if (!e.getPlayer().hasPermission("editworld")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDamageBlock(BlockDamageEvent e) {
        if (!e.getPlayer().hasPermission("editworld")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent e) {
        if (!e.getPlayer().hasPermission("editworld")) {
            e.setBuild(false);
            e.setCancelled(true);
        }
    }

}
