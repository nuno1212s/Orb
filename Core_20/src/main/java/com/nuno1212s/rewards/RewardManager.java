package com.nuno1212s.rewards;

import com.nuno1212s.main.MainData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages rewards
 */
public abstract class RewardManager {

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

    public void addReward(Reward r) {
        this.rewards.add(r);
    }

    public void redis_addReward(Reward r) {
        this.rewards.add(r);
    }

    public abstract void addRewardToClaim(Reward r);

    public abstract void redis_addRewardToClaim(int r);

    public abstract void createReward(Reward unfinishedReward);


}
