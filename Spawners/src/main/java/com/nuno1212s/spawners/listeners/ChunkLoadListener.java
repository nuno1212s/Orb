package com.nuno1212s.spawners.listeners;

import com.nuno1212s.spawners.entitybundle.EntityBundle;
import com.nuno1212s.spawners.main.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.List;

public class ChunkLoadListener implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {

        List<EntityBundle> unspawnedBundles = Main.getIns().getEntityManager().getUnspawnedBundles(e.getChunk());
        unspawnedBundles.forEach(EntityBundle::load);

    }

}
