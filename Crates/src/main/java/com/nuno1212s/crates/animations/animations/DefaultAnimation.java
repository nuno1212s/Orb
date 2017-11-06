package com.nuno1212s.crates.animations.animations;

import com.nuno1212s.crates.crates.Crate;
import com.nuno1212s.crates.Main;
import com.nuno1212s.crates.animations.Animation;
import com.nuno1212s.crates.crates.Reward;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * Default animation
 */
public class DefaultAnimation extends Animation {

    private final static List<Integer> displaySlots = Arrays.asList(4, 13, 22);

    public DefaultAnimation(Crate crate, Player player) {
        super(Bukkit.getServer().createInventory(null, 27, ChatColor.RED + crate.getCrateName()), crate, false, player);
    }

    private int iterations = 0, maxIterations = 100, tickInterval = 1, currentTick = 0;

    @Override
    public boolean run() {

        iterations++;

        if (++currentTick < tickInterval) {
            return false;
        }

        currentTick = 0;

        if (iterations % 5 == 0) {
            if (iterations >= 86) {
                tickInterval += 2;
            } else {
                tickInterval++;
            }
        }


        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);

        for (int i = 0; i < this.toEdit.getSize(); i++) {
            if (displaySlots.contains(i)) {
                continue;
            }
            toEdit.setItem(i, Main.getIns().getCrateManager().getAnimationManager().getRandomDisplayItem());
        }

        int current = displaySlots.size() - 1;

        for (;current>=0; current--) {
            if (current == 0) {
                toEdit.setItem(displaySlots.get(current), crate.getRandomReward().getDisplayItem());
            } else {
                toEdit.setItem(displaySlots.get(current), toEdit.getItem(displaySlots.get(current - 1)));
            }
        }

        if (iterations >= maxIterations) {
            this.finished = true;
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
            player.playSound(player.getLocation(), Sound.FIREWORK_BLAST, 1, 1);

            ItemStack rewardItem = toEdit.getItem(13);

            Reward reward = this.getCrate().getReward(rewardItem);

            reward.getItems().forEach(this.getPlayer().getInventory()::addItem);
        }

        return iterations >= maxIterations;
    }
}
