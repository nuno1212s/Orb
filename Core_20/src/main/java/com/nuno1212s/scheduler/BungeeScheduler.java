package com.nuno1212s.scheduler;

import lombok.AllArgsConstructor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.TaskScheduler;

import java.util.concurrent.TimeUnit;

/**
 * Bungee scheduler
 */
@AllArgsConstructor
public class BungeeScheduler implements Scheduler {

    TaskScheduler scheduler;

    Plugin p;

    @Override
    public void runTask(Runnable r) {
        scheduler.schedule(p, r, 2, TimeUnit.MILLISECONDS);
    }

    @Override
    public void runTaskAsync(Runnable r) {
        scheduler.runAsync(p, r);
    }

    @Override
    public void runTaskLater(Runnable r, long ticks) {
        scheduler.schedule(p, r, ticks * 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void runTaskLaterAsync(Runnable r, long ticks) {
        runTaskLater(r, ticks);
    }

    @Override
    public void runTaskTimer(Runnable r, long initDelay, long delay) {
        scheduler.schedule(p, r, initDelay * 50, delay * 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void runTaskTimerAsync(Runnable r, long initDelay, long delay) {
        runTaskTimer(r, initDelay, delay);
    }

}
