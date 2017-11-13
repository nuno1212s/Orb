package com.nuno1212s.events.listeners;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.Callback;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles player disconnect
 */
public class PlayerDisconnectListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        PlayerData p = MainData.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());

        p.setPlayerReference(null);

        MainData.getIns().getPermissionManager().getPlayerPermissions().unregisterPermissions(e.getPlayer());

        MainData.getIns().getServerManager().savePlayerCount(Bukkit.getOnlinePlayers().size() - 1, Bukkit.getMaxPlayers());

        if (!p.isShouldSave()) {
            MainData.getIns().getPlayerManager().removePlayer(p);
            return;
        }

        p.save((o) ->
            MainData.getIns().getPlayerManager().removePlayer(p)
        );
    }

}
