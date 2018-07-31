package com.nuno1212s.bungee.loginhandler.tasks;

import com.nuno1212s.bungee.events.BungeeCoreLogin;
import com.nuno1212s.bungee.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import net.md_5.bungee.api.ChatColor;
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
                connection.setOnlineMode(d.isAutoLogin());

                if (!d.isAutoLogin())
                    connection.setUniqueId(d.getPlayerID());

                d.setLastLogin(System.currentTimeMillis());

                BungeeCoreLogin coreLogin = new BungeeCoreLogin(d);

                Main.getPlugin().getProxy().getPluginManager().callEvent(coreLogin);
                d = coreLogin.getData();

                MainData.getIns().getPlayerManager().addToCache(d.getPlayerID(), d);

            } else {
                UUID premiumId = m.getConnector().getPremiumUUID(username);

                connection.setOnlineMode(false);

                if (premiumId == null) {
                    //Cracked account
                    UUID uuidForPlayer = UUID.randomUUID();

                    d = MainData.getIns().getPlayerManager().buildNewPiratePlayerData(uuidForPlayer, username);

                    BungeeCoreLogin event = new BungeeCoreLogin(d);
                    Main.getPlugin().getProxy().getPluginManager().callEvent(event);

                    d = event.getData();
                }  else {

                    d = MainData.getIns().getPlayerManager().buildNewPlayerData(premiumId, connection.getName());

                    //Check if player did not change name
                    PlayerData nameCheck = MainData.getIns().getMySql().getPlayerData(premiumId, null);

                    if (nameCheck != null) {
                        d = nameCheck;
                        d.setPlayerName(username);
                    }

                    BungeeCoreLogin event = new BungeeCoreLogin(d);
                    Main.getPlugin().getProxy().getPluginManager().callEvent(event);

                    d = event.getData();
                }

                connection.setUniqueId(d.getPlayerID());

                MainData.getIns().getPlayerManager().addToCache(d.getPlayerID(), d);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            event.completeIntent(Main.getPlugin());
        }
    }

}
