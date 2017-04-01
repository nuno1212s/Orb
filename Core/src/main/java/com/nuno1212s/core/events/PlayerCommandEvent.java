package com.nuno1212s.core.events;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Handles the commands
 */
public class PlayerCommandEvent implements Listener {

    @EventHandler
    public void commandPreProcess(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().startsWith("/me") || e.getMessage().startsWith("/minecraft:me") || e.getMessage().startsWith("/pl") || e.getMessage().startsWith("/bukkit:pl")
                || e.getMessage().startsWith("/?") || e.getMessage().startsWith("/help") || e.getMessage().startsWith("/bukkit:help") || e.getMessage().startsWith("/minecraft:help")
                || e.getMessage().startsWith("/bukkit:?") || e.getMessage().startsWith("/plugins")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "Comando não encontrado.");
        } else if (e.getMessage().startsWith("/op") || e.getMessage().startsWith("/stop")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "Este comando só pode ser feito pela consola!");
        }
    }

}
