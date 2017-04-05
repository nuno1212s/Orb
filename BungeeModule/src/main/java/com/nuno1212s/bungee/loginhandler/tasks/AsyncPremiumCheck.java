package com.nuno1212s.bungee.loginhandler.tasks;

import com.nuno1212s.bungee.events.BungeeCoreLogin;
import com.nuno1212s.bungee.main.Main;
import com.nuno1212s.events.CoreLoginEvent;
import com.nuno1212s.main.BungeeMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.playermanager.PlayerManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;

import java.util.UUID;

/**
 * Check if a player is premium
 */
public class AsyncPremiumCheck implements Runnable {

    private PreLoginEvent event;

    private Main m;

    public AsyncPremiumCheck(Main m, PreLoginEvent event) {
        this.m = m;
        this.event = event;
    }

    @Override
    public void run() {
        PendingConnection connection = event.getConnection();

        String username = connection.getName();

        PlayerData d = MainData.getIns().getMySql().getPlayerData(username);

        try {

            //Registered premium account
            if (d != null) {

                //UUID u = m.getConnector().getPremiumUUID(username);
                connection.setOnlineMode(d.isPremium());

                if (d.isPremium()) {
                    if (!connection.isOnlineMode()) {
                        event.setCancelled(true);
                        event.setCancelReason(ChatColor.RED + "You can't join with a premium username while using a cracked account!");
                        return;
                    }
                } else {
                    if (connection.isOnlineMode()) {
                        return;
                    }
                    connection.setUniqueId(d.getPlayerID());
                }

                d.setLastLogin(System.currentTimeMillis());

                MainData.getIns().getPlayerManager().addToCache(d.getPlayerID(), d);

            } else {
                UUID premiumId = m.getConnector().getPremiumUUID(username);

                connection.setOnlineMode(premiumId != null);

                if (premiumId == null) {
                    //Cracked account
                    UUID uuidForPlayer = UUID.randomUUID();
                    connection.setUniqueId(uuidForPlayer);
                    d = new PlayerData(connection.getUniqueId(),
                            MainData.getIns().getPermissionManager().getDefaultGroup().getGroupID(),
                            connection.getName(),
                            0,
                            System.currentTimeMillis(),
                            false);
                } else if (!connection.isOnlineMode()) {
                    event.setCancelled(true);
                    event.setCancelReason(ChatColor.RED + "You can't join with a premium username while using a cracked account!");
                    return;
                } else {

                    d = new PlayerData(premiumId,
                            MainData.getIns().getPermissionManager().getDefaultGroup().getGroupID(),
                            connection.getName(),
                            0,
                            System.currentTimeMillis(),
                            true);
                    //Check if player did not change name
                    PlayerData nameCheck = MainData.getIns().getMySql().getPlayerData(premiumId, null);
                    if (nameCheck != null) {
                        d = nameCheck;
                        d.setPlayerName(username);
                    }
                }

                MainData.getIns().getPlayerManager().addToCache(d.getPlayerID(), d);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            event.completeIntent(Main.getPlugin());
        }
    }

}
