package com.nuno1212s.homes.listeners;

import com.nuno1212s.homes.main.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Player quit events
 */
public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Main.getIns().getHomeManager().unloadPlayerHomes(e.getPlayer().getUniqueId());
    }

}
