package com.nuno1212s.homes.listeners;

import com.nuno1212s.homes.main.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Handles the player joining
 */
public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        long l = System.currentTimeMillis();

        Main.getIns().getFileManager().loadHomesForPlayer(e.getPlayer().getUniqueId());

        System.out.println(System.currentTimeMillis() - l);

    }

}
