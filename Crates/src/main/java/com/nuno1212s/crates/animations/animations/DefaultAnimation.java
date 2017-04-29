package com.nuno1212s.crates.animations.animations;

import com.nuno1212s.crates.Crate;
import com.nuno1212s.crates.Main;
import com.nuno1212s.crates.animations.Animation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

/**
 * Default animation
 */
public class DefaultAnimation extends Animation {

    final static List<Integer> displaySlots = Arrays.asList(4, 13, 22);

    public DefaultAnimation(Crate crate) {
        super(Bukkit.getServer().createInventory(null, 27, ChatColor.RED + crate.getCrateName()), crate);
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
