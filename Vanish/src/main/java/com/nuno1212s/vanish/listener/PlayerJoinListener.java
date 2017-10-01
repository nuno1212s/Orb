package com.nuno1212s.vanish.listener;

import com.nuno1212s.main.MainData;
import com.nuno1212s.vanish.main.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoinVanish(PlayerJoinEvent e) {
        Main.getIns().getVanishManager().handlePlayerJoin(e.getPlayer());
        MainData.getIns().getScheduler().runTaskAsync(() -> {
            Main.getIns().getPlayerManager().loadPlayer(e.getPlayer().getUniqueId());
        });
    }

}
