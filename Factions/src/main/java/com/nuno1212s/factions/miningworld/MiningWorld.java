package com.nuno1212s.factions.miningworld;

import com.nuno1212s.main.MainData;
import com.nuno1212s.util.Callback;
import com.nuno1212s.util.Pair;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class MiningWorld implements Listener {

    private String worldName;

    private long lastReset;

    private int size;

    public void createWorld(Callback<World> world) {
        MainData.getIns().getScheduler().runTaskAsync(() -> {

            WorldCreator w = new WorldCreator(this.worldName);
            World worldToGenerate = w.createWorld();

        });
    }

    public void loadChunks(World w, Callback<World> world) {
        MainData.getIns().getScheduler().runTaskTimer(new Runnable() {

            Pair<Integer, Integer> lastChunk;

            @Override
            public void run() {

            }
        }, 0, 1);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        e.getWorld().setKeepSpawnInMemory(false);
    }

}
