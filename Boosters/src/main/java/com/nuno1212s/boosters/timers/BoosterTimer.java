package com.nuno1212s.boosters.timers;

import com.nuno1212s.boosters.boosters.Booster;
import com.nuno1212s.boosters.main.Main;
import com.nuno1212s.main.MainData;

import java.util.Iterator;

/**
 * Handles boosters expiration
 */
public class BoosterTimer implements Runnable {

    public BoosterTimer() {
        MainData.getIns().getScheduler().runTaskTimer(this, 10, 1200);
    }

    @Override
    public void run() {
        synchronized (Main.getIns().getBoosterManager().getBoosters()) {
            Iterator<Booster> iterator = Main.getIns().getBoosterManager().getBoosters().iterator();

            while (iterator.hasNext()) {
                Booster b = iterator.next();
                if (b.isExpired()) {
                    iterator.remove();
                    Main.getIns().getBoosterManager().handleBoosterExpiration(b);
                }
            }
        }

    }
}
