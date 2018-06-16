package com.nuno1212s.npccommands.listeners;

import com.nuno1212s.npccommands.main.Main;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerInteractEntityListener implements Listener {

    private List<UUID> npcInteractions = new ArrayList<>();

    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEntityEvent e) {
        Entity rightClicked = e.getRightClicked();

        if (Main.getIns().getNpcManager().getCommandsFromNPC(rightClicked.getUniqueId()) != null) {
            e.setCancelled(true);
            npcInteractions.add(e.getPlayer().getUniqueId());
            Main.getIns().getNpcManager().executeCommands(rightClicked.getUniqueId(), e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(PlayerInteractEvent e) {
        if (npcInteractions.contains(e.getPlayer().getUniqueId())) {
            npcInteractions.remove(e.getPlayer().getUniqueId());
            e.setCancelled(true);
        }
    }

}
