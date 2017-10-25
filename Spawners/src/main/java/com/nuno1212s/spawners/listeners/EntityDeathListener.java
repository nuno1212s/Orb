package com.nuno1212s.spawners.listeners;

import com.nuno1212s.spawners.entitybundle.EntityBundle;
import com.nuno1212s.spawners.main.Main;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener implements Listener {

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
