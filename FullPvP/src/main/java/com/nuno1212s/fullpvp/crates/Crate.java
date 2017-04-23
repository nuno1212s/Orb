package com.nuno1212s.fullpvp.crates;

import com.nuno1212s.fullpvp.crates.animations.Animation;
import com.nuno1212s.fullpvp.crates.animations.animations.DefaultAnimation;
import com.nuno1212s.fullpvp.main.Main;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;
import java.util.Set;

/**
 * The crate information
 */
@Getter
public class Crate {

    private String crateName;

    private Set<Reward> rewards;

    public Crate(String crateName, Set<Reward> rewards) {
        this.crateName = crateName;
        this.rewards = rewards;

        recalculateProbabilities();

        System.out.println(rewards);
    }

    public void recalculateProbabilities() {
        if (this.rewards.isEmpty()) {
            return;
        }
        int currentFullProbability = 0;
        for (Reward reward : this.rewards) {
            currentFullProbability += (int) reward.getOriginalProbability();
        }

        double multiplier = 0;

        /*
        currentFullProbability = 1
        100 = multiplier
         */

        if (currentFullProbability != 100) {
            multiplier = (100D / currentFullProbability);
        }

        for (Reward reward : this.rewards) {
            reward.recalculateProbability(multiplier);
        }
    }

    public boolean removeReward(int rewardID) {
        return this.rewards.removeIf(r -> r.getRewardID() == rewardID);
    }

    public int getNextRewardID() {
        int maxID = -1;
        for (Reward reward : this.rewards) {
            int rewardID = reward.getRewardID();
            if (rewardID > maxID) {
                maxID = rewardID;
            }
        }
        return ++maxID;
    }

    public void openCase(Player p) {

        Animation animation = Main.getIns().getCrateManager().getAnimationManager().getRandomAnimation(this);

        Main.getIns().getCrateManager().getAnimationManager().registerAnimation(animation);

        p.openInventory(animation.getToEdit());
    }

    public ItemStack getRandomReward() throws Exception {
        Random r = new Random();

        double v = r.nextDouble() * 100, currently = 0;

        for (Reward reward : this.rewards) {
            if (v > currently && v <= (currently += reward.getProbability())) {
                return reward.getItem().clone();
            }
        }

        return null;
    }

}
