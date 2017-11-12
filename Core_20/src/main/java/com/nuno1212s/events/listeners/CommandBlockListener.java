package com.nuno1212s.events.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandBlockListener implements Listener {

    @EventHandler
    public void onCommandProcess(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().startsWith("/minecraft:")
                || e.getMessage().startsWith("/bukkit:")
                || e.getMessage().startsWith("/me")
                || e.getMessage().startsWith("/pl")
                || e.getMessage().startsWith("/plugins")
                || e.getMessage().startsWith("/ver")
                || e.getMessage().startsWith("/version")
                || e.getMessage().startsWith("/say")) {
            e.setCancelled(true);
        }
    }

}
