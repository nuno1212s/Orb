package com.nuno1212s.boosters.boosters;

import com.nuno1212s.boosters.main.Main;
import com.nuno1212s.boosters.playerdata.BoosterData;
import com.nuno1212s.main.MainData;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Booster
 */
@Getter
@AllArgsConstructor
public class Booster {

    String boosterID;

    BoosterType type;

    double multiplier;

    long durationInMillis, activationTime;

    boolean activated;

    String applicableServer;

    public void activate() {
        this.activated = true;
        this.activationTime = System.currentTimeMillis();

        Main.getIns().getRedisHandler().handleBoosterActivation(this);

    }

    public boolean isApplicable(BoosterData data) {

        if (data != null && type != BoosterType.GLOBAL_GLOBAL && type != BoosterType.GLOBAL_SERVER) {
            if (!data.getBoosters().contains(boosterID)) {
                return false;
            }
        }

        String serverType = MainData.getIns().getServerManager().getServerType();
        return (type == BoosterType.GLOBAL_GLOBAL || type == BoosterType.PLAYER_GLOBAL)
                || (serverType.equalsIgnoreCase(applicableServer) && (type == BoosterType.GLOBAL_SERVER || type == BoosterType.PLAYER_SERVER));
    }

}
