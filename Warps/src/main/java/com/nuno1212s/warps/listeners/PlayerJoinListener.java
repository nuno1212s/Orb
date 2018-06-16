package com.nuno1212s.warps.listeners;

import com.nuno1212s.warps.main.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Handles player joining the server
 */
public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Main.getIns().getFileManager().loadHomesForPlayer(e.getPlayer().getUniqueId());
    }


}
