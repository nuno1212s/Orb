package com.nuno1212s.events.war.listeners;

import com.nuno1212s.events.EventMain;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.UUID;

public class PlayerDisconnectListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {

        List<UUID> playersRegistered = EventMain.getIns().getWarEvent().getPlayersRegistered();

        if (playersRegistered.contains(e.getPlayer().getUniqueId())) {

        }

    }

}
