package com.nuno1212s.spawners.listeners;

import com.nuno1212s.spawners.main.Main;
import com.nuno1212s.spawners.playerdata.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Removes player information when the player disconnects
 */
public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        PlayerData player = Main.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());

        if (player != null) {
            Main.getIns().getPlayerManager().removePlayer(player);
        }

    }

}
