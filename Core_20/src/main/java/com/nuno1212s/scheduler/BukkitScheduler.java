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
    public void runTaskLater(Runnable r, long ticks) {
        scheduler.runTaskLater(p, r, ticks);
    }

    @Override
    public void runTaskLaterAsync(Runnable r, long ticks) {
        scheduler.runTaskLaterAsynchronously(p, r, ticks);
    }

    @Override
    public void runTaskTimer(Runnable r, long initDelay, long delay) {
        scheduler.runTaskTimer(p, r, initDelay, delay);
    }

    @Override
    public void runTaskTimerAsync(Runnable r, long initDelay, long delay) {
        scheduler.runTaskTimerAsynchronously(p, r, initDelay, delay);
    }
}
