package com.nuno1212s.fullpvp.crates.animations;

import com.nuno1212s.fullpvp.crates.Reward;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.List;

/**
 * Default animation
 */
public class DefaultAnimation extends Animation {

    public DefaultAnimation(Inventory inv, List<Reward> rewards) {
        super(inv, rewards);
    }

    final static List<Integer> displaySlots = Arrays.asList(5, 14, 23);

    @Override
    public boolean run() {
        for (int i = 0; i < this.toEdit.getSize(); i++) {
            //toEdit.setItem(i, );
        }
        return false;
    }
}
