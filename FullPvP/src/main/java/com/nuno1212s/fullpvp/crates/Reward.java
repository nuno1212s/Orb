package com.nuno1212s.fullpvp.crates;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

/**
 * Reward class
 */
public class Reward {

    @Getter
    double probability;

    ItemStack item;

    public Reward(ItemStack item, int probability) {
        this.item = item.clone();
        this.probability = probability;
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
