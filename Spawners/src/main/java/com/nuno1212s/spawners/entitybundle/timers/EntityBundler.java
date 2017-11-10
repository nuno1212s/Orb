package com.nuno1212s.spawners.entitybundle.timers;

import com.nuno1212s.main.MainData;
import com.nuno1212s.spawners.entitybundle.EntityBundle;
import com.nuno1212s.spawners.main.Main;

import java.util.*;

public class EntityBundler implements Runnable {

    public EntityBundler() {
        //We can run this task async because we will onl be using our API's, any entity removal needs to be done sync
        MainData.getIns().getScheduler().runTaskTimerAsync(this, 0, 20);
    }

    @Override
    public void run() {
        Map<String, List<EntityBundle>> spawnedEntitiesByWorld = Main.getIns().getEntityManager().getSpawnedEntitiesByWorld();

        spawnedEntitiesByWorld.forEach(
                (world, entities) -> {

                    int currentIt = 0;

                    EntityBundle currentlyHandling = null;

                    while (currentIt < entities.size()) {
                        ListIterator<EntityBundle> entityBundleListIterator = entities.listIterator(currentIt++);

                        if (!entityBundleListIterator.hasNext()) {
                            break;
                        }

                        if (currentlyHandling == null) {
                            currentlyHandling = entityBundleListIterator.next();
                            if (!currentlyHandling.isLoaded()) {
                                continue;
                            }
                        }

                        while (entityBundleListIterator.hasNext()) {
                            EntityBundle toCompare = entityBundleListIterator.next();

                            if (toCompare.getType() == currentlyHandling.getType()) {
                                if (toCompare.isLoaded() && currentlyHandling.isLoaded()) {
                                    if (toCompare.getEntityReference().getLocation()
                                            .distanceSquared(currentlyHandling.getEntityReference().getLocation()) < 16) {
                                        currentlyHandling = merge(currentlyHandling, toCompare);
                                        //Remove the compared one to avoid repeated comparisons
                                        entityBundleListIterator.remove();
                                    }
                                }
                            }

                        }

                        entities.set(currentIt - 1, currentlyHandling);
                        currentlyHandling = null;
                    }

                }
        );

        Main.getIns().getEntityManager().acceptNewEntities(spawnedEntitiesByWorld);
    }

    /**
     * Merge the entities
     *
     * @param e  The first entity
     * @param e2 The second entity
     * @return
     */
    private EntityBundle merge(EntityBundle e, EntityBundle e2) {
        e.addToBundle(e2.getMobCount());

        MainData.getIns().getScheduler().runTask(e2::forceRemove);

        return e;
    }
}
