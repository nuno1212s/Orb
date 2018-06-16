package com.nuno1212s.minas.timers;

import com.nuno1212s.minas.main.Main;
import com.nuno1212s.minas.minemanager.Mine;

import java.util.List;

/**
 * Resets timers
 */
public class ResetTimers implements Runnable {

    @Override
    public void run() {
        List<Mine> mines = Main.getIns().getMineManager().getMines();

        for (Mine mine : mines) {
            if (mine.shouldReset()) {
                mine.resetMine();
            }
        }

    }
}
