package com.nuno1212s.rankup.events;

import com.nuno1212s.displays.Main;
import com.nuno1212s.main.MainData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Player disconnect listener
 */
public class PlayerDisconnectListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void playerDisconnect(PlayerQuitEvent e) {
        Main.getIns().getScoreboardManager().handlePlayerDC(MainData.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId()));
    }

}
