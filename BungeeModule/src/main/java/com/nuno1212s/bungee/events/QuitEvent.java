package com.nuno1212s.bungee.events;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Handles the disconnection event
 */
public class QuitEvent implements Listener {

    @EventHandler
    public void onQuit(PlayerDisconnectEvent e) {
        PlayerData player = MainData.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());

        if (player != null) {
            MainData.getIns().getPlayerManager().removePlayer(player);
        }

    }

}
