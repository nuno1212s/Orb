package com.nuno1212s.scheduler;

/**
 * Global scheduler
 */
public interface Scheduler {

    void runTask(Runnable r);

    int runTaskLater(Runnable r, long ticks);

    void runTaskAsync(Runnable r);

    int runTaskLaterAsync(Runnable r, long ticks);

    int runTaskTimer(Runnable r, long initDelay, long delay);

    int runTaskTimerAsync(Runnable r, long initDelay, long delay);

}
