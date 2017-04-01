package com.nuno1212s.events;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.playermanager.PlayerManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

/**
 *  Handles plugin messages
 */
public class PluginMessage implements Listener {

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) {
        String channel = e.getTag();
        if (channel.equalsIgnoreCase("TELLINFO")) {
            e.setCancelled(true);
            byte[] data = e.getData();

            ByteArrayDataInput dataInput = ByteStreams.newDataInput(data);
            ProxiedPlayer p = ProxyServer.getInstance().getPlayer(dataInput.readUTF());
            if (p == null || !p.isConnected()) {
                return;
            }
            PlayerData d = PlayerManager.getIns().getPlayer(p.getUniqueId());
            d.setTell(Boolean.valueOf(dataInput.readUTF()));
        } else if (channel.equalsIgnoreCase("GROUPUPDATE")) {
            e.setCancelled(true);
            byte[] data = e.getData();

            ByteArrayDataInput dataInput = ByteStreams.newDataInput(data);
            ProxiedPlayer p = ProxyServer.getInstance().getPlayer(UUID.fromString(dataInput.readUTF()));
            if (p == null || !p.isConnected()) {
                return;
            }
            PlayerData d = PlayerManager.getIns().getPlayer(p.getUniqueId());
            d.setGroupId(dataInput.readShort());
        }
    }

}
