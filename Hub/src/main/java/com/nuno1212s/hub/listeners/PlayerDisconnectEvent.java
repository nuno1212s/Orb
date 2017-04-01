package com.nuno1212s.hub.listeners;

import com.nuno1212s.core.playermanager.PlayerManager;
import com.nuno1212s.hub.scoreboard.ScoreboardHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles player disconnect events
 */
public class PlayerDisconnectEvent implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        ScoreboardHandler.getIns().handlePlayerDC(e.getPlayer(), PlayerManager.getIns().getPlayerData(e.getPlayer().getUniqueId()));
    }

}
