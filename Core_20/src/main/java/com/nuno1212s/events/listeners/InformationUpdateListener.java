package com.nuno1212s.events.listeners;

import com.nuno1212s.events.PlayerInformationUpdateEvent;
import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.PlayerPermissions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class InformationUpdateListener implements Listener {

    @EventHandler
    public void onUpdate(PlayerInformationUpdateEvent e) {

        if (e.getEventReason() == PlayerInformationUpdateEvent.Reason.GROUP_UPDATE
                || e.getEventReason() == PlayerInformationUpdateEvent.Reason.UNDETERMINED) {

            Player playerReference = e.getPlayer().getPlayerReference(Player.class);
            PlayerPermissions playerPermissions = MainData.getIns().getPermissionManager().getPlayerPermissions();
            playerPermissions.unregisterPermissions(playerReference);
            playerPermissions.injectPermission(playerReference, e.getPlayer());

        }

    }

}
