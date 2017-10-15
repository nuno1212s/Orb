package com.nuno1212s.factions.miningworld;

import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.util.Callback;
import com.nuno1212s.util.Pair;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

@NoArgsConstructor
public class WorldCreateTask implements Runnable {

    private Pair<Integer, Integer> currentChunk;

    private int current = 0, length = 0, x = 0, z = 0, completeCurrent = 0, completeLength;

    private boolean isZLeg = true, isNeg = false;

    private long currentTimeTaken = 0;

    @Setter
    private transient BukkitTask task;

    @Setter
    private transient World w;

    @Setter
    private transient Callback<World> callback;

    public WorldCreateTask(World w, Callback<World> callback, int size) {
        this.w = w;
        this.callback = callback;
        this.completeLength = (int) Math.pow(size >> 4, 2);

        Location spawnLocation = w.getSpawnLocation();
        currentChunk = new Pair<>(spawnLocation.getBlockX() >> 4, spawnLocation.getBlockZ() >> 4);
        task = BukkitMain.getIns().getServer().getScheduler().runTaskTimer(BukkitMain.getIns(), this, 2, 1);

    }

    /**
     * Get the next chunk to load
     *
     * @return The next chunk to load
     */
    private Pair<Integer, Integer> getNextChunk() {

        if (current < length) {
            current++;
            completeCurrent++;

            if (completeCurrent > completeLength) {
                return null;
            }

        } else {
            this.current = 0;

            this.isZLeg ^= true;

            if (this.isZLeg) {
                this.isNeg ^= true;
                length++;
            }

        }

        if (isZLeg) {
            z += (isNeg) ? -1 : 1;
        } else {
            x += (isNeg) ? -1 : 1;
        }

        this.currentChunk = new Pair<>(x, z);

        return this.currentChunk;
    }

    @Override
    public void run() {

        while (currentTimeTaken < 25) {

            long initTime = System.currentTimeMillis();

            Pair<Integer, Integer> nextChunk = getNextChunk();

            if (nextChunk == null) {
                System.out.println("Loaded World.");
                task.cancel();
                this.callback.callback(this.w);
                return;
            }

            w.loadChunk(nextChunk.getKey(), nextChunk.getValue(), true);

            //System.out.println("Loaded chunk " + nextChunk.toString() + " in " + String.valueOf(System.currentTimeMillis() - initTime) + "ms");

            currentTimeTaken += System.currentTimeMillis() - initTime;
        }

        currentTimeTaken = 0;

    }
}
