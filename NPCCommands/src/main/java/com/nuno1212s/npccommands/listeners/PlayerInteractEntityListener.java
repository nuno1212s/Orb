package com.nuno1212s.npccommands.listeners;

import com.nuno1212s.npccommands.main.Main;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerInteractEntityListener implements Listener {

    @EventHandler(ignoreCancelled = false)
    public void onPlayerInteract(PlayerInteractEntityEvent e) {
        Entity rightClicked = e.getRightClicked();

        if (Main.getIns().getNpcManager().getCommandsFromNPC(rightClicked.getUniqueId()) != null) {
            e.setCancelled(true);
            Main.getIns().getNpcManager().executeCommands(rightClicked.getUniqueId(), e.getPlayer());
        }
    }

}
