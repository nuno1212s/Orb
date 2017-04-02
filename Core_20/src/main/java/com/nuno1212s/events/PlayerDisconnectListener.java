package com.nuno1212s.events;

import com.nuno1212s.main.Main;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.Callback;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles player disconnect
 */
public class PlayerDisconnectListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        PlayerData p = Main.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());

        Main.getIns().getPermissionManager().unregisterPermissions(e.getPlayer());

        p.save(new Callback() {
            @Override
            public void callback(Object... args) {
                Main.getIns().getPlayerManager().removePlayer(p);
            }
        });

    }

}
