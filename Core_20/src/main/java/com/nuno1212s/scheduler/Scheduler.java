package com.nuno1212s.scheduler;

/**
 * Global scheduler
 */
public interface Scheduler {

    void runTask(Runnable r);

    void runTaskLater(Runnable r, long ticks);

    void runTaskAsync(Runnable r);

    void runTaskLaterAsync(Runnable r, long ticks);

    void runTaskTimer(Runnable r, long initDelay, long delay);

    void runTaskTimerAsync(Runnable r, long initDelay, long delay);

}
