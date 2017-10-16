package com.nuno1212s.hub.listeners;

import com.nuno1212s.hub.main.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        Main.getIns().getServerSelectorManager().handlePlayerDisconnect(e.getPlayer());
    }

}
