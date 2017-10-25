package com.nuno1212s.spawners.listeners;

import com.nuno1212s.spawners.entitybundle.EntityBundle;
import com.nuno1212s.spawners.main.Main;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkUnloadListener implements Listener {

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        Entity[] entities = e.getChunk().getEntities();

        for (Entity entity : entities) {
            EntityBundle entityBundle = Main.getIns().getEntityManager().getEntityBundle(entity);

            if (entityBundle == null) {
                return;
            }

            entityBundle.unload();

        }

    }

}
