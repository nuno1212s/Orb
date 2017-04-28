package com.nuno1212s.rankup.crates;

import lombok.Getter;
import lombok.ToString;
import org.bukkit.inventory.ItemStack;

/**
 * Reward class
 */
@ToString
public class Reward {

    @Getter
    private int rewardID;

    @Getter
    double probability;

    @Getter
    final double originalProbability;

    @Getter
    private ItemStack item;

    public Reward(int rewardID, ItemStack item, int probability) {
        this.rewardID = rewardID;
        this.item = item.clone();
        this.probability = probability;
        this.originalProbability = probability;
    }

    /**
     * Initial probability will be given in a 0-X form.
     *
     * Do the calculations to turn it into a 0-100
     * Simple rule of three
     */
    public void recalculateProbability(double divider) {
        this.probability = originalProbability * divider;
    }

}
