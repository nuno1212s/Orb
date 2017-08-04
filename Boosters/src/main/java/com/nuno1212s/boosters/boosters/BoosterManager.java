package com.nuno1212s.boosters.boosters;

import com.nuno1212s.boosters.playerdata.BoosterData;
import com.nuno1212s.multipliers.main.RankMultiplierMain;
import com.nuno1212s.playermanager.PlayerData;
import org.apache.commons.lang.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all boosters
 */
public class BoosterManager {

    private List<Booster> boosters;

    public BoosterManager() {
        boosters = new ArrayList<>();
    }

    private String getRandomID() {
        String random = RandomStringUtils.random(5, true, true);
        if (getBooster(random) != null) {
            return getRandomID();
        }
        return random;
    }

    public Booster createBooster(double multiplier, long durationInMillis, BoosterType type, String applicableServer) {
        return new Booster(getRandomID(), type, multiplier, durationInMillis, 0, false, applicableServer);
    }

    public Booster getBooster(String boosterID) {
        for (Booster booster : boosters) {
            if (booster.getBoosterID().equalsIgnoreCase(boosterID)) {
                return booster;
            }
        }
        return null;
    }

    public double getMCMMOMultiplierForPlayer(PlayerData data) {
        double currentBooster = RankMultiplierMain.getIns().getRankManager().getGlobalMultiplier().getRankMultiplierForPlayer(data);

        if (data instanceof BoosterData) {
            for (Booster booster : boosters) {
                if (booster.isActivated() && booster.isApplicable((BoosterData) data)) {
                    currentBooster += booster.getMultiplier();
                }
            }
        } else {
            for (Booster booster : boosters) {
                if (booster.isActivated() && booster.isApplicable(null)) {
                    currentBooster += booster.getMultiplier();
                }
            }
        }

        return currentBooster;
    }

    public boolean isBoosterActive(PlayerData data) {
        if (data instanceof BoosterData) {
            for (Booster booster : boosters) {
                if (booster.isActivated() && booster.isApplicable((BoosterData) data)) {
                    return true;
                }
            }
        } else {
            for (Booster booster : boosters) {
                if (booster.isApplicable(null) && booster.isActivated()) {
                    return true;
                }
            }
        }
        return false;
    }

}
