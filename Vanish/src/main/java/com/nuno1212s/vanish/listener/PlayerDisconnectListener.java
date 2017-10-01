package com.nuno1212s.vanish.listener;

import com.nuno1212s.main.MainData;
import com.nuno1212s.vanish.main.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDisconnectListener implements Listener {

    @EventHandler
    public void onQuitVanish(PlayerQuitEvent e) {
        MainData.getIns().getScheduler().runTaskAsync(() -> {
            Main.getIns().getPlayerManager().unloadPlayer(e.getPlayer().getUniqueId());
        });
    }

}
