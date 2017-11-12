package com.nuno1212s.spawners.listeners;

import com.nuno1212s.spawners.entitybundle.EntityBundle;
import com.nuno1212s.spawners.events.PlayerKillBundledEntityEvent;
import com.nuno1212s.spawners.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onKill(EntityDamageEvent e) {
        if (e.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) e.getEntity();

            if (entity.getHealth() - e.getFinalDamage() <= 0) {
                EntityBundle entityBundle = Main.getIns().getEntityManager().getEntityBundle(entity);

                if (entityBundle == null) {
                    return;
                }

                if (entityBundle.getMobCount() > 1) {
                    if (!Main.getIns().getEntityManager().handleDeath(entityBundle)) {
                        entity.setHealth(entity.getMaxHealth());
                        e.setCancelled(true);
                        entity.damage(0);

                        if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                            Bukkit.getPluginManager().callEvent(new PlayerKillBundledEntityEvent((EntityDamageByEntityEvent) e));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        EntityBundle entityBundle = Main.getIns().getEntityManager().getEntityBundle(entity);

        if (entityBundle != null) {
            e.getDrops().clear();
            Main.getIns().getEntityManager().handleDeath(entityBundle);
        }

    }

}
