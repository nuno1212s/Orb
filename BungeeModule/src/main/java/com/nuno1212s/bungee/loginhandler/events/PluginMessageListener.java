package com.nuno1212s.bungee.loginhandler.events;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.nuno1212s.bungee.loginhandler.SessionData;
import com.nuno1212s.bungee.loginhandler.SessionHandler;
import com.nuno1212s.bungee.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

/**
 * Listens to the plugin messages
 */
public class PluginMessageListener implements Listener{

    private Plugin m;

    public PluginMessageListener() {
        this.m = Main.getPlugin();
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent listener) {
        String channel = listener.getTag();
        if (listener.isCancelled() || !channel.equalsIgnoreCase("AUTOLOGIN")) {
            return;
        }

        listener.setCancelled(true);

        if (Server.class.isAssignableFrom(listener.getSender().getClass())) {

            byte[] data = listener.getData();

            ByteArrayDataInput dataInput = ByteStreams.newDataInput(data);

            String s = dataInput.readUTF();
            if (s.equalsIgnoreCase("AUTHENTICATE")) {
                String playerName = dataInput.readUTF();

                ProxiedPlayer p = ProxyServer.getInstance().getPlayer(playerName);

                if (p == null || !p.isConnected()) {
                    return;
                }

                PlayerData d = MainData.getIns().getPlayerManager().getPlayer(playerName);

                UUID oldId = d.getPlayerID();

                d.setPremium(true);
                //d.set(p.getUniqueId());

                ProxyServer.getInstance().getScheduler().runAsync(m, () -> {
                    //TODO: MySqlHandler.getIns().updatePlayer(oldId, d);
                });

                //TODO: SEND NOTICE TO ALL SERVERS TO UPDATE DATABASES

            } else if (s.equalsIgnoreCase("LOGIN")) {
                String playerName = dataInput.readUTF();

                ProxiedPlayer p = ProxyServer.getInstance().getPlayer(playerName);

                if (p == null || !p.isConnected()) {
                    return;
                }

                if (SessionHandler.getIns().getSession(p.getUniqueId()) == null) {
                    SessionHandler.getIns().addSession(new SessionData(p.getUniqueId(), true));
                } else {
                    SessionHandler.getIns().updateSession(p.getUniqueId(), new SessionData(p.getUniqueId(), true));
                }

            }

        }
    }

}
