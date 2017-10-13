package com.nuno1212s.factions.miningworld;

import com.nuno1212s.factions.main.Main;
import com.nuno1212s.main.MainData;

public class WorldExpireTask implements Runnable {

    public WorldExpireTask() {
        MainData.getIns().getScheduler().runTaskTimerAsync(this, 1200, 1200);
    }

    @Override
    public void run() {
        long lastReset = Main.getIns().getMiningWorld().getLastReset();

        if (lastReset + Main.getIns().getMiningWorld().getBetweenResets() < System.currentTimeMillis() && !Main.getIns().getMiningWorld().isLoading()) {
            Main.getIns().getMiningWorld().regenerateWorld();
        }

    }
}
