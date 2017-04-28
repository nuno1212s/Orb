package com.nuno1212s.rankup.events;

import com.nuno1212s.events.PlayerInformationUpdateEvent;
import com.nuno1212s.rankup.main.Main;
import com.nuno1212s.rankup.playermanager.PVPPlayerData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Player update listeners
 */
public class PlayerUpdateListener implements Listener {

    @EventHandler
    public void onUpdate(PlayerInformationUpdateEvent e) {
        PlayerData player = e.getPlayer();
        Main.getIns().getScoreboardManager().createScoreboard((PVPPlayerData) player, Bukkit.getPlayer(player.getPlayerID()));
    }

}
