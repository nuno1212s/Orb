package com.nuno1212s.events.listeners;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.Callback;
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

        MainData.getIns().getPermissionManager().getPlayerPermissions().unregisterPermissions(e.getPlayer());

        if (!p.isShouldSave()) {
            MainData.getIns().getPlayerManager().removePlayer(p);
            return;
        }

        p.save(new Callback() {
            @Override
            public void callback(Object... args) {
                MainData.getIns().getPlayerManager().removePlayer(p);
            }
        });

    }

}
