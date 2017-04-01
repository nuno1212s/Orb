package com.nuno1212s.loginhandling.events;

import com.nuno1212s.main.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import com.nuno1212s.loginhandling.SessionHandler;
import com.nuno1212s.loginhandling.tasks.AsyncPremiumCheck;
import com.nuno1212s.loginhandling.tasks.ForceLoginTask;

/**
 * Handles player connections
 */
public class PlayerLoginEvent implements Listener {

    private Main m;

    public PlayerLoginEvent(Main m) {
        this.m = m;
    }

    @EventHandler
    public void loginEvent(PreLoginEvent e) {
        if (e.isCancelled()) {
            return;
        }
        e.registerIntent(m);
        ProxyServer.getInstance().getScheduler().runAsync(m, new AsyncPremiumCheck(m, e));
    }


    @EventHandler
    public void serverConnect(ServerConnectedEvent serverConnect) {
        ProxiedPlayer p = serverConnect.getPlayer();
        ForceLoginTask task = new ForceLoginTask(m, p, serverConnect.getServer());
        ProxyServer.getInstance().getScheduler().runAsync(m, task);
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent dc) {
        ProxiedPlayer player = dc.getPlayer();
        //m.getSession().remove(player.getPendingConnection());
        SessionHandler.getIns().removeSession(player.getUniqueId());
    }

}
