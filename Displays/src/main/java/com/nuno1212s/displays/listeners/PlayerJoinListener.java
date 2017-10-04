package com.nuno1212s.displays.listeners;

import com.nuno1212s.displays.DisplayMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Handles players joining
 */
public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        PlayerData player = MainData.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        DisplayMain.getIns().getScoreboardManager().handlePlayerJoin(player, e.getPlayer());
    }
}
