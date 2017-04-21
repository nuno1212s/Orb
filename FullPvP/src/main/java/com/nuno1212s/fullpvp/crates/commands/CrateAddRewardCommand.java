package com.nuno1212s.fullpvp.crates.commands;

import com.nuno1212s.fullpvp.crates.Crate;
import com.nuno1212s.fullpvp.crates.Reward;
import com.nuno1212s.fullpvp.main.Main;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Add reward crate command
 */
public class CrateAddRewardCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"addreward", "ar"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/crate addreward <crateName> <percentage>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (player.hasPermission("crate.addreward")) {
            return;
        }
        if (args.length < 3) {
            player.sendMessage(this.usage());
            return;
        }

        String crateName = args[1], percentage = args[2];

        Crate c = Main.getIns().getCrateManager().getCrate(crateName);

        if (c == null) {
            player.sendMessage(ChatColor.RED + "That crate does not exist.");
            return;
        }

        int percentageI;

        try {
            percentageI = Integer.parseInt(percentage);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Percentage must be a number");
            return;
        }
        Reward r = new Reward(c.getNextRewardID(), player.getItemInHand().clone(), percentageI);

        c.getRewards().add(r);

        c.recalculateProbabilities();

        player.sendMessage(String.format(ChatColor.RED + "Reward added ID: " + r.getRewardID() + " . Reward Probability: " + "%.3f %", r.getProbability()));

    }
}
