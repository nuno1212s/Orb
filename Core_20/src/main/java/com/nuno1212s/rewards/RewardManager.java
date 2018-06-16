package com.nuno1212s.rewards;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages rewards
 */
public abstract class RewardManager {

    @Getter
    protected final List<Reward> rewards;

    protected RedisRewards redisHandler;

    public RewardManager() {
        rewards = Collections.synchronizedList(new ArrayList<>());

        redisHandler = new RedisRewards();

        MainData.getIns().getRedisHandler().registerRedisListener(this.redisHandler);
    }

    /**
     * Get a reward with a certain id
     *
     * @param id
     * @return
     */
    public Reward getReward(int id) {

        synchronized (rewards) {
            for (Reward r : rewards) {
                if (r.getId() == id) {
                    return r;
                }
            }
        }

        return null;
    }

    /**
     * Get the default rewards for a new player
     * @return
     */
    public List<Integer> getDefaultRewards() {
        List<Integer> claimers = new ArrayList<>();

        for (Reward reward : rewards) {
            if (reward.isDefault()) {
                claimers.add(reward.getId());
            }
        }

        return claimers;
    }

    /**
     * Add a reward to the reward list
     *
     * @param r
     */
    public void addReward(Reward r) {
        this.rewards.add(r);
    }

    public void redis_addReward(Reward r) {
        this.rewards.add(r);
    }

    /**
     * Remove a reward from the reward list
     * @param r
     */
    public void removeReward(Reward r) {
        this.rewards.remove(r);

        for (PlayerData playerData : MainData.getIns().getPlayerManager().getPlayers()) {
            playerData.claim(r.getId());
        }

        this.redisHandler.removeReward(r.getId());
        MainData.getIns().getMySql().removeReward(r.getId());
    }

    public void redis_removeReward(Reward r) {
        this.rewards.remove(r);

        for (PlayerData playerData : MainData.getIns().getPlayerManager().getPlayers()) {
            playerData.claim(r.getId());
        }

    }

    public abstract void addRewardToClaim(Reward r);

    public abstract void redis_addRewardToClaim(int r);

    public abstract void createReward(Reward unfinishedReward);


}
