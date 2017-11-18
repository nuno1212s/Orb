package com.nuno1212s.spawners.listeners;

import com.nuno1212s.main.MainData;
import com.nuno1212s.spawners.entitybundle.EntityBundle;
import com.nuno1212s.spawners.main.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;

public class MobSpawnListener implements Listener {

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
            e.setCancelled(true);

            EntityBundle nearestEntityBundleTo =
                    Main.getIns().getEntityManager().getNearestEntityBundleTo(e.getEntity(), 5);

            System.out.println(nearestEntityBundleTo);

            nearestEntityBundleTo.addToBundle(1);

        }
    }

    @EventHandler
    public void mobSpawn(SpawnerSpawnEvent e) {
        MainData.getIns().getScheduler().runTaskLater(() -> {
            e.getSpawner().setDelay(0);
        }, Main.getIns().getRewardManager().getSpawnerSpacing());

    }

}
