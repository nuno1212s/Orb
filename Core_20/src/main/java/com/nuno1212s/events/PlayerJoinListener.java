package com.nuno1212s.events;

import com.nuno1212s.main.Main;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Handles player data loading
 */
public class PlayerJoinListener implements Listener {

    /**
     * Load the main player information
     *
     * @param e
     */
    @EventHandler
    public void onPlayerLogin(AsyncPlayerPreLoginEvent e) {
        PlayerData coreData = Main.getIns().getMySql().getPlayerData(e.getUniqueId(), e.getName());
        CoreLoginEvent event = new CoreLoginEvent(e, coreData);
        Bukkit.getServer().getPluginManager().callEvent(event);
        //After the event being called, all the modules had the opportunity to modify the player data class
        PlayerData finalData = event.getPlayerInfo();
        Main.getIns().getPlayerManager().addToCache(e.getUniqueId(), finalData);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Main.getIns().getPlayerManager().validatePlayerJoin(e.getPlayer().getUniqueId());
    }

}
