package com.nuno1212s.duels.listeners;

import com.nuno1212s.duels.DuelMain;
import com.nuno1212s.duels.duelmanager.Duel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntityListener implements Listener {

    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent e) {

        Duel activeDuelForPlayer = DuelMain.getIns().getDuelManager().getActiveDuelForPlayer(e.getDamager().getUniqueId());

        if (activeDuelForPlayer != null) {

            if (activeDuelForPlayer.isSpectator(e.getDamager().getUniqueId())) {

                e.setCancelled(true);

            }

        }

    }

}
