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

}
