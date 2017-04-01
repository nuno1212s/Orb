package com.nuno1212s.events;

import com.nuno1212s.mysql.MySqlHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import com.nuno1212s.main.Main;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.playermanager.PlayerManager;

/**
 * Handles login
 */
public class LoginEvent implements Listener {

    private Main m;

    public LoginEvent(Main m) {
        this.m = m;
    }


    @EventHandler
    public void onLogin(net.md_5.bungee.api.event.LoginEvent e) {
        if (e.isCancelled()) {
            return;
        }
        e.registerIntent(m);
        if (e.getConnection().getUniqueId() != null) {
            PlayerData d = PlayerManager.getIns().getTempData(e.getConnection().getUniqueId());
            if (d == null) {
                e.setCancelled(true);
                e.setCancelReason("Invalid Session. Please restart minecraft.");
                e.completeIntent(m);
                return;
            }
            PlayerData player = PlayerManager.getIns().getPlayer(d.getUuid());
            if (player != null) {
                PlayerManager.getIns().removePlayer(player);
            }
            d.setLastIp(e.getConnection().getAddress().toString());
            PlayerManager.getIns().removeTempData(d.getUuid());
            PlayerManager.getIns().addPlayer(d);
            ProxyServer.getInstance().getScheduler().runAsync(m, () -> {
                        MySqlHandler.getIns().savePlayer(d);
                        e.completeIntent(m);
                    }
            );

        }

    }

    /**
     * Handles the login event and loads the player information
     *
     * @param login -> The login event
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PostLoginEvent login) {
        //System.out.println(MySqlHandler.getIns().getPlayerData(login.getPlayer().getUniqueId()));
        /*PlayerData d = PlayerManager.getIns().getTempData(login.getPlayer().getUniqueId());
        PlayerManager.getIns().removeTempData(login.getPlayer().getUniqueId());
        PlayerManager.getIns().addPlayer(d);
        ProxyServer.getInstance().getScheduler().runAsync(m, () ->
                MySqlHandler.getIns().savePlayer(d)
        );*/
    }
}
