package com.nuno1212s.rewards.bungee;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.rewards.Reward;
import com.nuno1212s.rewards.RewardManager;

/**
 * Manages bungee rewards
 */
public class BungeeRewardManager extends RewardManager {

    public BungeeRewardManager() {
        rewards.addAll(MainData.getIns().getMySql().getBungeeRewards());
    }

    @Override
    public void addRewardToClaim(Reward r) {
        //No need to fill this out, as rewards are not created in this instance
    }

    @Override
    public void redis_addRewardToClaim(int r) {
        for (PlayerData playerData : MainData.getIns().getPlayerManager().getPlayers()) {
            playerData.addToClaim(r);
        }
    }

    @Override
    public void createReward(Reward unfinishedReward) {

    }
}

