package com.nuno1212s.rewards.bukkit;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.rewards.RedisRewards;
import com.nuno1212s.rewards.Reward;
import com.nuno1212s.rewards.RewardManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages reward
 */
public class BukkitRewardManager extends RewardManager {

    public BukkitRewardManager() {
        super();

        rewards.addAll(MainData.getIns().getMySql().getRewards());

    }

    /**
     *
     * @param r
     */
    @Override
    public void addReward(Reward r) {
        super.addReward(r);
        this.redisHandler.publishNewReward((BukkitReward) r);
    }

    /**
     *
     * @param r
     */
    @Override
    public void addRewardToClaim(Reward r) {
        for (PlayerData playerData : MainData.getIns().getPlayerManager().getPlayers()) {
            playerData.addToClaim(r.getId());
            MainData.getIns().getMessageManager().getMessage("RECEIVED_REWARD").sendTo(playerData);
        }

        MainData.getIns().getMySql().addRewardToClaim(r.getId());
        this.redisHandler.addRewardToClaim(r.getId());
    }

    public void redis_addRewardToClaim(int r) {
        for (PlayerData playerData : MainData.getIns().getPlayerManager().getPlayers()) {
            playerData.addToClaim(r);
            MainData.getIns().getMessageManager().getMessage("RECEIVED_REWARD").sendTo(playerData);
        }
    }

    /**
     *
     * @param unfinishedReward
     */
    public void createReward(Reward unfinishedReward) {
        MainData.getIns().getScheduler().runTaskAsync(() -> {
            int i = MainData.getIns().getMySql().saveReward((BukkitReward) unfinishedReward);
            unfinishedReward.setId(i);
            addReward(unfinishedReward);
            addRewardToClaim(unfinishedReward);
        });
    }

}
