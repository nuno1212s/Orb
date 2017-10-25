package com.nuno1212s.spawners.listeners;

import com.nuno1212s.spawners.entitybundle.EntityBundle;
import com.nuno1212s.spawners.main.Main;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class MobKillListener implements Listener {

    @EventHandler
    public void onKill(EntityDamageEvent e) {
        LivingEntity entity = (LivingEntity) e.getEntity();
        if (entity.getHealth() - e.getFinalDamage() <= 0) {
            EntityBundle entityBundle = Main.getIns().getEntityManager().getEntityBundle(entity);

            if (entityBundle != null) {
                e.setCancelled(true);

                entityBundle.kill();
            }
        }

    }

}