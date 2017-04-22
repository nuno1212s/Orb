package com.nuno1212s.fullpvp.crates.commands;

import com.nuno1212s.fullpvp.crates.Crate;
import com.nuno1212s.fullpvp.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Remove a reward from a crate
 */
public class CrateRemoveRewardCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"removereward", "rw"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/crate removereward <crateName> <rewardID>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("crate.removereward")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(player);
            return;
        }

        if (args.length < 3) {
            player.sendMessage(this.usage());
            return;
        }

        String crateName = args[1];

        Crate crate = Main.getIns().getCrateManager().getCrate(crateName);

        if (crate == null) {
            player.sendMessage(ChatColor.RED + "A crate with that name does not exist");
            return;
        }

        int rewardID;

        try {
            rewardID = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "The reward ID must be a number");
            return;
        }

        if (crate.removeReward(rewardID)) {
            player.sendMessage(ChatColor.RED + "Reward has been removed successfully.");
        } else {
            player.sendMessage(ChatColor.RED + "A reward with that name does not exist.");
        }

    }
}
