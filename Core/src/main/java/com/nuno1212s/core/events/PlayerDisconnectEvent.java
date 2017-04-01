package com.nuno1212s.core.events;

import com.nuno1212s.core.main.Main;
import com.nuno1212s.core.playermanager.PlayerData;
import com.nuno1212s.core.mysql.MySqlDB;
import com.nuno1212s.core.playermanager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles player disconnects
 */
public class PlayerDisconnectEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDisconnect(PlayerQuitEvent e) {
        PlayerData playerData = PlayerManager.getIns().getPlayerData(e.getPlayer().getUniqueId());
        PlayerManager.getIns().removePlayer(playerData);
        if (playerData.isChangedSinceLastSave()) {
            if (!(playerData.getSavedd() != null && playerData.getSavedd().getKey() && System.currentTimeMillis() - playerData.getSavedd().getValue() < 1000)) {
                Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                    MySqlDB.getIns().updatePlayerData(playerData);
                });
            }
        }
    }

}
