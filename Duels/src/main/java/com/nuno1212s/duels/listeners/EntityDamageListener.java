package com.nuno1212s.duels.listeners;

import com.nuno1212s.duels.DuelMain;
import com.nuno1212s.duels.duelmanager.Duel;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {

        if (e.getEntityType() == EntityType.PLAYER) {

            Duel d = DuelMain.getIns().getDuelManager().getActiveDuelForPlayer(e.getEntity().getUniqueId());

            if (d != null) {

                if (d.isSpectator(e.getEntity().getUniqueId())) {
                    e.setCancelled(true);

                    return;
                }

                if (!d.isDamageEnabled()) {
                    e.setCancelled(true);
                } else {

                    if (((LivingEntity) e.getEntity()).getHealth() - e.getFinalDamage() <= 0) {

                        e.setCancelled(true);

//                        ((Player) e.getEntity()).spigot().setCollidesWithEntities(false);
// TODO: 19-11-2018 Make player go into spectator mode
                    }

                }

            }

        }

    }

}
