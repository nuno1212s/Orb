package com.nuno1212s.crates.crates;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Reward class
 */
@ToString
@NoArgsConstructor
public class Reward {

    @Getter
    private int rewardID;

    @Getter
    double probability;

    @Getter
    transient double originalProbability;

    @Getter
    private List<ItemStack> items;

    @Getter
    private ItemStack displayItem;

    public Reward(int rewardID, ItemStack displayItem, ItemStack item, int probability) {
        this.rewardID = rewardID;
        this.displayItem = displayItem.clone();
        this.items = new ArrayList<>();
        this.items.add(item.clone());
        this.probability = probability;
        this.originalProbability = probability;
    }

    public Reward(int rewardID, ItemStack displayItem, int probability) {
        this.rewardID = rewardID;
        this.displayItem = displayItem.clone();
        this.probability = probability;
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
