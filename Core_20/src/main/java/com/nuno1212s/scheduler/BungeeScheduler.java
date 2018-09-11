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
    public int runTaskLater(Runnable r, long ticks) {
        return scheduler.schedule(p, r, ticks * 50, TimeUnit.MILLISECONDS).getId();
    }

    @Override
    public int runTaskLaterAsync(Runnable r, long ticks) {
        return runTaskLater(r, ticks);
    }

    @Override
    public int runTaskTimer(Runnable r, long initDelay, long delay) {
        return scheduler.schedule(p, r, initDelay * 50, delay * 50, TimeUnit.MILLISECONDS).getId();
    }

    @Override
    public int runTaskTimerAsync(Runnable r, long initDelay, long delay) {
        return runTaskTimer(r, initDelay, delay);
    }

}
