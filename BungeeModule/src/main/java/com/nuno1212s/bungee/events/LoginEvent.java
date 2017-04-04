package com.nuno1212s.bungee.events;

import com.nuno1212s.bungee.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.Callback;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Handles after the premium check login events
 */
public class LoginEvent implements Listener {

    @EventHandler
    public void onLogin(net.md_5.bungee.api.event.LoginEvent e) {
        if (e.isCancelled()) {
            return;
        }

        e.registerIntent(Main.getPlugin());

        if (e.getConnection().getUniqueId() != null) {
            PlayerData cachedPlayer = MainData.getIns().getPlayerManager().getCachedPlayer(e.getConnection().getUniqueId());
            if (cachedPlayer == null) {
                e.setCancelled(true);
                e.setCancelReason("Invalid Session. Please restart minecraft.");
                e.completeIntent(Main.getPlugin());
                return;
            }

            cachedPlayer.setLastLogin(System.currentTimeMillis());

            MainData.getIns().getPlayerManager().validatePlayerJoin(cachedPlayer.getPlayerID());
            cachedPlayer.save(new Callback() {
                @Override
                public void callback(Object... args) {
                    e.completeIntent(Main.getPlugin());
                }
            });

        }
    }

}
