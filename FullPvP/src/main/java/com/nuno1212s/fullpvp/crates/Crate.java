package com.nuno1212s.fullpvp.crates;

import com.nuno1212s.fullpvp.crates.animations.Animation;
import com.nuno1212s.fullpvp.crates.animations.DefaultAnimation;
import com.nuno1212s.fullpvp.main.Main;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * The crate information
 */
@Getter
public class Crate {

    private String crateName;

    private List<Reward> rewards;

    public Crate(String crateName, List<Reward> rewards) {
        this.crateName = crateName;
        this.rewards = rewards;

        recalculateProbabilities();

    }

    public void recalculateProbabilities() {
        int currentFullProbability = 0;
        for (Reward reward : this.rewards) {
            currentFullProbability += (int) reward.getOriginalProbability();
        }

        int multiplier = 0;

        /*
        currentFullProbability = 1
        100 = multiplier
         */

        if (currentFullProbability != 100) {
            multiplier = (100 / currentFullProbability);
        }

        for (Reward reward : this.rewards) {
            reward.recalculateProbability(multiplier);
        }
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

    void openCase(Player p) {
        Inventory i = Bukkit.getServer().createInventory(null, 54, ChatColor.RED + crateName);
        p.openInventory(i);

        Animation animation = new DefaultAnimation(i, this.rewards);

        Main.getIns().getCrateManager().getAnimationManager().registerAnimation(animation);


    }

}
