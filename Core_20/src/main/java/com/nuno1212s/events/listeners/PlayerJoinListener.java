package com.nuno1212s.events.listeners;

import com.nuno1212s.events.CoreLoginEvent;
import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;
import com.nuno1212s.playermanager.CorePlayerData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

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
            coreData = new CorePlayerData(e.getUniqueId(), new PlayerGroupData(), e.getName(),
                    0, System.currentTimeMillis(), true,
                    MainData.getIns().getRewardManager().getDefaultRewards(), null);
        }

        CoreLoginEvent event = new CoreLoginEvent(e, coreData);
        Bukkit.getServer().getPluginManager().callEvent(event);
        //After the event being called, all the modules had the opportunity to modify the player data class
        PlayerData finalData = event.getPlayerInfo();
        MainData.getIns().getPlayerManager().addToCache(e.getUniqueId(), finalData);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        if (e.getResult() == PlayerLoginEvent.Result.KICK_FULL) {
            PlayerData cachedPlayer = MainData.getIns().getPlayerManager().getCachedPlayer(e.getPlayer().getUniqueId());
            Group playerMainGroup = cachedPlayer.getMainGroup();
            if (playerMainGroup.hasPermission("vip") || playerMainGroup.hasPermission("staff")) {
                e.setResult(PlayerLoginEvent.Result.ALLOWED);
            }

        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void corePlayerJoin(PlayerJoinEvent e) {
        PlayerData d = MainData.getIns().getPlayerManager().validatePlayerJoin(e.getPlayer().getUniqueId());

        d.setPlayerReference(e.getPlayer());

        MainData.getIns().getPermissionManager().getPlayerPermissions().injectPermission(e.getPlayer(), d);
        MainData.getIns().getServerManager().savePlayerCount(Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
    }

}
