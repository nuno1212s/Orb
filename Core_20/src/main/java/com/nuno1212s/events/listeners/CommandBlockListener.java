package com.nuno1212s.events.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandBlockListener implements Listener {

    @EventHandler
    public void onCommandProcess(PlayerCommandPreprocessEvent e) {
        String message = e.getMessage().toLowerCase();
        if (message.startsWith("/minecraft:")
                || message.startsWith("/bukkit:")
                || message.startsWith("/me")
                || message.startsWith("/pl")
                || message.startsWith("/plugins")
                || message.startsWith("/ver")
                || message.startsWith("/version")
                || message.startsWith("/say")) {
            e.setCancelled(true);
        }
    }

}
