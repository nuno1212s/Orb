package com.nuno1212s.boosters.listeners;

import com.nuno1212s.boosters.main.Main;
import com.nuno1212s.main.MainData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

/**
 * Handles players logging in and loads their boosters
 */
public class PlayerLoginListener implements Listener {

    @EventHandler (priority = EventPriority.HIGH)
    public void onJoin(PlayerLoginEvent e) {
        if (e.getResult() == PlayerLoginEvent.Result.ALLOWED) {
            UUID uniqueId = e.getPlayer().getUniqueId();
            MainData.getIns().getScheduler().runTaskAsync(() -> {
                Main.getIns().getBoosterManager().loadBoostersForPlayer(uniqueId);
            });
        }
    }

}
