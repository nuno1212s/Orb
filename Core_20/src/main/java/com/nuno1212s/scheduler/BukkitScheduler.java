package com.nuno1212s.scheduler;

import lombok.AllArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Bukkit scheduler
 */
@AllArgsConstructor
public class BukkitScheduler implements Scheduler {

    private org.bukkit.scheduler.BukkitScheduler scheduler;

    private JavaPlugin p;

    @Override
    public void runTask(Runnable r) {
        scheduler.runTask(p, r);
    }

    @Override
    public void runTaskAsync(Runnable r) {
        scheduler.runTaskAsynchronously(p, r);
    }

    @Override
    public int runTaskLater(Runnable r, long ticks) {
        return scheduler.runTaskLater(p, r, ticks).getTaskId();
    }

    @Override
    public int runTaskLaterAsync(Runnable r, long ticks) {
        return scheduler.runTaskLaterAsynchronously(p, r, ticks).getTaskId();
    }

    @Override
    public int runTaskTimer(Runnable r, long initDelay, long delay) {
        return scheduler.runTaskTimer(p, r, initDelay, delay).getTaskId();
    }

    @Override
    public int runTaskTimerAsync(Runnable r, long initDelay, long delay) {
        return scheduler.runTaskTimerAsynchronously(p, r, initDelay, delay).getTaskId();
    }
}
