package com.nuno1212s.displays.listeners;

import com.nuno1212s.displays.DisplayMain;
import com.nuno1212s.events.PlayerGroupUpdateEvent;
import com.nuno1212s.events.PlayerInformationUpdateEvent;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerUpdateListener implements Listener {

    @EventHandler
    public void onUpdate(PlayerInformationUpdateEvent e) {
        PlayerData player = e.getPlayer();
        Player player1 = player.getPlayerReference(Player.class);

        if (player1 != null) {
            DisplayMain.getIns().getScoreboardManager().createScoreboard(player, player1);
        }
    }

    @EventHandler
    public void onGroupUpdate(PlayerGroupUpdateEvent e) {
        PlayerData player = e.getPlayer();
        Player player1 = player.getPlayerReference(Player.class);

        if (player1 != null) {
            DisplayMain.getIns().getScoreboardManager().handlePlayerGroupUpdate(player, e.getPreviousGroup());
        }


    }

}
