package com.nuno1212s.rankup.events;

import com.nuno1212s.events.PlayerInformationUpdateEvent;
import com.nuno1212s.main.MainData;
import com.nuno1212s.rankup.main.Main;
import com.nuno1212s.rankup.playermanager.RUPlayerData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Player update listeners
 */
public class PlayerUpdateListener implements Listener {

    @EventHandler
    public void onUpdate(PlayerInformationUpdateEvent e) {
        PlayerData player = e.getPlayer();
        Player player1 = Bukkit.getPlayer(player.getPlayerID());
        if (player1 != null) {
            com.nuno1212s.displays.Main.getIns().getScoreboardManager().createScoreboard(player, player1);
        }
    }

}
