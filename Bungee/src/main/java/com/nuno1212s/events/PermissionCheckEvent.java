package com.nuno1212s.events;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import com.nuno1212s.loginhandling.SessionHandler;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.playermanager.PlayerManager;

/**
 * Handles permission checks
 */
public class PermissionCheckEvent implements Listener {

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onChat(net.md_5.bungee.api.event.PermissionCheckEvent e) {
        if (e.getSender() instanceof ProxiedPlayer) {
            PlayerData d = PlayerManager.getIns().getPlayer(((ProxiedPlayer) e.getSender()).getUniqueId());
            if (!d.isPremium() && (SessionHandler.getIns().getSession(((ProxiedPlayer) e.getSender()).getUniqueId()) == null || !SessionHandler.getIns().getSession(((ProxiedPlayer) e.getSender()).getUniqueId()).isAuthenticated())) {
                e.setHasPermission(false);
            }
        }
    }

}
