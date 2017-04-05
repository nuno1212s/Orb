package com.nuno1212s.bungee.events;

import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.playermanager.PlayerData;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * Handles permissions
 */
public class PermissionCheckListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPermissionCheck(PermissionCheckEvent e) {
        if (e.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) e.getSender();
            PlayerData player = MainData.getIns().getPlayerManager().getPlayer(p.getUniqueId());

            Group mainGroup = player.getMainGroup();

            e.setHasPermission(mainGroup.hasPermission(e.getPermission()));

        }
    }

}
