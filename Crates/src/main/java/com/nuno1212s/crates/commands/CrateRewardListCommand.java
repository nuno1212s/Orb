package com.nuno1212s.crates.commands;

import com.nuno1212s.crates.crates.Crate;
import com.nuno1212s.crates.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Lists the rewards of a certain crate
 */
public class CrateRewardListCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"rewardlist"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/crate rewardlist <crateName>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("crate.listreward")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(player);
            return;
        }

        if (args.length < 2) {
            player.sendMessage(this.usage());
            return;
        }

        String crateName = args[1];

        Crate crate = Main.getIns().getCrateManager().getCrate(crateName);

        if (crate == null) {
            player.sendMessage(ChatColor.RED + "A crate with that name does not exist");
            return;
        }

        crate.getRewards().forEach(reward -> {
            player.sendMessage("ID:" + reward.getRewardID());
            player.sendMessage("Probability: " + reward.getProbability());
            player.sendMessage("Item:" + reward.getItem());
        });
    }
}
