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

                    ListIterator<EntityBundle> iterator = entities.listIterator();

                    while (iterator.hasNext()) {
                        EntityBundle next = iterator.next();

                        if (iterator.hasNext()) {

                            ListIterator<EntityBundle> checkingAfterTheEntities = entities.listIterator(iterator.nextIndex());

                            while (checkingAfterTheEntities.hasNext()) {
                                EntityBundle next1 = checkingAfterTheEntities.next();

                                if (next1.getType() == next.getType()) {

                                    if (next.getEntityReference().getLocation().distanceSquared(next1.getEntityReference().getLocation()) < 10) {
                                        iterator.set(merge(next, next1));
                                        checkingAfterTheEntities.remove();
                                        break;
                                    }
                                }
                            }
                        } else {
                            break;
                        }
                    }

                }
        );

    }

    /**
     * Merge the entities
     *
     * @param e The first entity
     * @param e2 The second entity
     * @return
     */
    private EntityBundle merge(EntityBundle e, EntityBundle e2) {
        e.addToBundle(e2.getMobCount());

        MainData.getIns().getScheduler().runTask(e2::forceRemove);

        return e;
    }
}
