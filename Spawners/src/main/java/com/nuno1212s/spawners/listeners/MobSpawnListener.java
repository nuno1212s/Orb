package com.nuno1212s.spawners.listeners;

import com.nuno1212s.spawners.entitybundle.EntityBundle;
import com.nuno1212s.spawners.main.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MobSpawnListener implements Listener {

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
            e.setCancelled(true);

            EntityBundle nearestEntityBundleTo =
                    Main.getIns().getEntityManager().getNearestEntityBundleTo(e.getEntity(), 5);
            nearestEntityBundleTo.addToBundle(1);

        }
    }

}
