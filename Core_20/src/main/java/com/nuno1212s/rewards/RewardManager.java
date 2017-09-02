package com.nuno1212s.rewards;

import com.nuno1212s.main.MainData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages reward
 */
public class RewardManager {

    private final List<Reward> rewards;

    public RewardManager() {
        rewards = Collections.synchronizedList(new ArrayList<>());
        rewards.addAll(MainData.getIns().getMySql().getRewards());
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
     *
     * @param r
     */
    public void addReward(Reward r) {
        this.rewards.add(r);
    }

    /**
     *
     * @param unfinishedReward
     */
    public void createReward(Reward unfinishedReward) {
        MainData.getIns().getScheduler().runTaskAsync(() -> {
            int i = MainData.getIns().getMySql().saveReward(unfinishedReward);
            unfinishedReward.setId(i);
            addReward(unfinishedReward);
            MainData.getIns().getMySql().addRewardToClaim(i);
        });
    }

}
