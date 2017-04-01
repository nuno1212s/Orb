package com.nuno1212s.core.events;

import com.nuno1212s.core.playermanager.PlayerData;
import com.nuno1212s.core.playermanager.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Handles AFK
 */
public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getTo().getBlockY() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
            PlayerData d = PlayerManager.getIns().getPlayerData(e.getPlayer().getUniqueId());
            d.setLastMovement(System.currentTimeMillis());
        }
    }

}
