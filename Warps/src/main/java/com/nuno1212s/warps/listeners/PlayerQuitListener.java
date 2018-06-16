package com.nuno1212s.warps.listeners;

import com.nuno1212s.warps.main.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles player disconnecting from the server
 */
public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (Main.getIns().getTeleportTimer().isTeleporting(e.getPlayer().getUniqueId())) {
            Main.getIns().getTeleportTimer().cancelTeleport(e.getPlayer().getUniqueId());
        }
        Main.getIns().getHomeManager().unloadPlayerHomes(e.getPlayer().getUniqueId());
    }


}
