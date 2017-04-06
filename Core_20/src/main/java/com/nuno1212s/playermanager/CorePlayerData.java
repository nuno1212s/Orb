package com.nuno1212s.playermanager;

import com.nuno1212s.main.MainData;
import com.nuno1212s.util.Callback;

import java.util.UUID;

/**
 * The core data of players
 */
public class CorePlayerData extends PlayerData {

    public CorePlayerData(UUID playerID, short groupID, String playerName, long cash, long lastLogin, boolean premium) {
        super(playerID, groupID, playerName, cash, lastLogin, premium);
    }

    @Override
    public void save(Callback c) {
        MainData.getIns().getScheduler().runTaskAsync(() -> {
            MainData.getIns().getMySql().savePlayer(this);
            c.callback();
        });
    }

    @Override
    public short getServerGroup() {
        return -1;
    }
}
