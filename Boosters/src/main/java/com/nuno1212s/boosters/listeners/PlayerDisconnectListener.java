package com.nuno1212s.boosters.listeners;

import com.nuno1212s.boosters.main.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles player disconnects
 */
public class PlayerDisconnectListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Main.getIns().getBoosterManager().removeBoostersForPlayer(e.getPlayer().getUniqueId());
    }

}
