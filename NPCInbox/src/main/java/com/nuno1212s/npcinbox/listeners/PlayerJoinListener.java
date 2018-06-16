package com.nuno1212s.npcinbox.listeners;

import com.nuno1212s.main.MainData;
import com.nuno1212s.npcinbox.main.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        Main.getIns().getNpcManager().displayNotificationsForPlayer(MainData.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId()));
    }

}
