package com.nuno1212s.fullpvp.crates.animations;

import com.nuno1212s.fullpvp.crates.Crate;
import com.nuno1212s.fullpvp.crates.Reward;
import com.nuno1212s.fullpvp.main.Main;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.List;

/**
 * Default animation
 */
public class DefaultAnimation extends Animation {

    final static List<Integer> displaySlots = Arrays.asList(4, 13, 22);

    public DefaultAnimation(Inventory inv, Crate crate) {
        super(inv, crate);
    }

    private int iterations = 0;

    @Override
    public boolean run() {
        iterations++;
        for (int i = 0; i < this.toEdit.getSize(); i++) {
            if (displaySlots.contains(i)) {
                continue;
            }
            toEdit.setItem(i, Main.getIns().getCrateManager().getAnimationManager().getRandomDisplayItem());
        }
        displaySlots.forEach(it -> {
            try {
                toEdit.setItem(it, crate.getRandomReward());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return iterations > 1200;
    }
}
