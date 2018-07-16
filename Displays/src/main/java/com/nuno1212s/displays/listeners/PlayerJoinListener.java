package com.nuno1212s.displays.listeners;

import com.nuno1212s.displays.DisplayMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Handles players joining
 */
public class PlayerJoinListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        PlayerData player = MainData.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        DisplayMain.getIns().getScoreboardManager().handlePlayerJoin(player, e.getPlayer());
        DisplayMain.getIns().getTabManager().sendDisplay(e.getPlayer());

        for (PlayerData d : MainData.getIns().getPlayerManager().getPlayers()) {

            if (!d.getPlayerID().equals(e.getPlayer().getUniqueId())) {
                DisplayMain.getIns().getScoreboardManager().createScoreboard(d, d.getPlayerReference(Player.class));
            }
        }


    }
}
