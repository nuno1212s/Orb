package com.nuno1212s.loginhandling.tasks;

import com.nuno1212s.main.Main;
import com.nuno1212s.mysql.MySqlHandler;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.playermanager.PlayerManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;
import com.nuno1212s.permissions.PermissionsAPI;

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

        PlayerData d = MySqlHandler.getIns().getPlayerData(username);

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
                    connection.setUniqueId(d.getUuid());
                }

                d.setLastLogin(System.currentTimeMillis());
                //PlayerManager.getIns().addPlayer(d);
                PlayerManager.getIns().addTemporaryPlayer(d);

            } else {
                UUID premiumId = m.getConnector().getPremiumUUID(username);

                connection.setOnlineMode(premiumId != null);

                if (premiumId == null) {
                    //Cracked account
                    UUID uuidForPlayer = UUID.randomUUID();
                    connection.setUniqueId(uuidForPlayer);
                    d = new PlayerData(connection.getName(), connection.getUniqueId(), /*DEFAULT GROUP*/ (short) PermissionsAPI.getIns().getDefaultGroup().getGroupId(), false, "", System.currentTimeMillis(), true);
                } else if (!connection.isOnlineMode()) {
                    event.setCancelled(true);
                    event.setCancelReason(ChatColor.RED + "You can't join with a premium username while using a cracked account!");
                } else {
                    d = new PlayerData(connection.getName(), premiumId, PermissionsAPI.getIns().getDefaultGroup().getGroupId(), true, "", System.currentTimeMillis(), true);

                    //Check if player did not change name
                    PlayerData nameCheck = MySqlHandler.getIns().getPlayerData(premiumId);
                    if (nameCheck != null) {
                        d = nameCheck;
                        d.setName(username);
                    }
                }
                //PlayerManager.getIns().addPlayer(d);
                PlayerManager.getIns().addTemporaryPlayer(d);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            event.completeIntent(m);
        }
    }

}
