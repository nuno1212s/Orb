package com.nuno1212s.displays.listeners;

import com.nuno1212s.displays.DisplayMain;
import com.nuno1212s.main.MainData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDisconnectListener implements Listener {

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        DisplayMain.getIns().getScoreboardManager().handlePlayerDC(MainData.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId()));
    }

}
