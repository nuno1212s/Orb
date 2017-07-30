package com.nuno1212s.spawners.playerdata;

import com.nuno1212s.main.MainData;
import com.nuno1212s.spawners.main.Main;

import java.util.List;

/**
 * Handles timers
 */
public class Timer implements Runnable {

    public Timer() {
        MainData.getIns().getScheduler().runTaskTimer(this, 0, 20);
    }

    @Override
    public void run() {
        List<PlayerData> players = Main.getIns().getPlayerManager().getPlayers();

        players.removeIf(player -> {
            if (player.shouldDiscard()) {
                player.discard();
                return true;
            }

            if (player.shouldMessage()) {
                player.sendMessage();
                return false;
            }

            return false;
        });

    }
}
