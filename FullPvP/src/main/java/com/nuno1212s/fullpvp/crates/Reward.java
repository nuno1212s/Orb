package com.nuno1212s.fullpvp.crates;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

/**
 * Reward class
 */
public class Reward {

    @Getter
    private int rewardID;

    @Getter
    double probability;

    @Getter
    final double originalProbability;

    private ItemStack item;

    public Reward(int rewardID, ItemStack item, int probability) {
        this.rewardID = rewardID;
        this.item = item.clone();
        this.probability = probability;
        this.originalProbability = getOriginalProbability();
    }

    /**
     * Initial probability will be given in a 0-X form.
     *
     * Do the calculations to turn it into a 0-100
     * Simple rule of three
     */
    public void recalculateProbability(int divider) {
        this.probability *= divider;
    }

}
