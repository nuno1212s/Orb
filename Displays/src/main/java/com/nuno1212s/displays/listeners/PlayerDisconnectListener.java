package com.nuno1212s.displays.listeners;

import com.nuno1212s.displays.DisplayMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDisconnectListener implements Listener {

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        DisplayMain.getIns().getScoreboardManager().handlePlayerDC(MainData.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId()));

        synchronized (MainData.getIns().getPlayerManager().getPlayers()) {
            for (PlayerData d : MainData.getIns().getPlayerManager().getPlayers()) {
                if (!d.getPlayerID().equals(e.getPlayer().getUniqueId())) {
                    DisplayMain.getIns().getScoreboardManager().createScoreboard(d, d.getPlayerReference(Player.class));
                }
            }
        }


    }

}
