package com.nuno1212s.hub.listeners;

import com.nuno1212s.hub.main.Main;
import com.nuno1212s.hub.npcs.NPC;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerInteractEntityListener implements Listener {

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof LivingEntity) {
            NPC npc = Main.getIns().getNpcManager().getNPC((LivingEntity) e.getRightClicked());

            e.setCancelled(true);
            npc.handleClick(e.getPlayer());

        }
    }

}
