package com.nuno1212s.events.listeners;

import com.nuno1212s.events.CoreLoginEvent;
import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;
import com.nuno1212s.playermanager.CorePlayerData;
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
        PlayerData coreData = MainData.getIns().getMySql().getPlayerData(e.getUniqueId(), e.getName());
        if (coreData == null) {
            coreData = new CorePlayerData(e.getUniqueId(), new PlayerGroupData(), e.getName(), 0, System.currentTimeMillis(), true);
        }
        CoreLoginEvent event = new CoreLoginEvent(e, coreData);
        Bukkit.getServer().getPluginManager().callEvent(event);
        //After the event being called, all the modules had the opportunity to modify the player data class
        PlayerData finalData = event.getPlayerInfo();
        MainData.getIns().getPlayerManager().addToCache(e.getUniqueId(), finalData);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        MainData.getIns().getPlayerManager().validatePlayerJoin(e.getPlayer().getUniqueId());
        MainData.getIns().getPermissionManager().getPlayerPermissions().injectPermission(e.getPlayer());
    }

}