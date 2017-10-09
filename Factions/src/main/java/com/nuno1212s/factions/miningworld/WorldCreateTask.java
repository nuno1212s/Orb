package com.nuno1212s.factions.miningworld;

import com.nuno1212s.util.Pair;
import org.bukkit.Location;
import org.bukkit.World;

public class WorldCreateTask implements Runnable {

    private World w;

    private Pair<Integer, Integer> currentChunk;

    public WorldCreateTask(World w) {
        this.w = w;

        Location spawnLocation = w.getSpawnLocation();
        currentChunk = new Pair<>(spawnLocation.getBlockX() >> 4, spawnLocation.getBlockZ() >> 4);

    }

    public Pair<Integer, Integer> getNextChunk() {
        return null;
    }

    @Override
    public void run() {

    }
}
