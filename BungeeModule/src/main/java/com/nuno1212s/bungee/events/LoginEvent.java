package com.nuno1212s.bungee.events;

import com.nuno1212s.bungee.main.Main;
import com.nuno1212s.bungee.playermanager.BungeePlayerData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.punishments.Punishment;
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

            Punishment punishment = cachedPlayer.getPunishment();

            if (punishment != null
                    && punishment.getPunishmentType() == Punishment.PunishmentType.BAN
                    && !punishment.hasExpired()) {
                MainData.getIns().getPlayerManager().removeCachedPlayer(cachedPlayer.getPlayerID());
                e.setCancelled(true);
                e.setCancelReason(punishment.buildReason());
                e.completeIntent(Main.getPlugin());
                return;
            }

            MainData.getIns().getPlayerManager().validatePlayerJoin(cachedPlayer.getPlayerID());
            cachedPlayer.save((arg) ->
                    e.completeIntent(Main.getPlugin())
            );

        }
    }

    @EventHandler
    public void onBungeeLogin(BungeeCoreLogin core) {
        core.setData(new BungeePlayerData(core.getData()));
    }

}
