package com.nuno1212s.npcinbox.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.rewards.Reward;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RemoveRewardCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"remove"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/reward remove <id> - Removes a reward with the given ID";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(usage());
            return;
        }

        int id;

        try {
            id = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Reward ID must be a number");
            return;
        }

        Reward r = MainData.getIns().getRewardManager().getReward(id);

        if (r == null) {
            player.sendMessage(ChatColor.RED + "No reward with that ID");
            return;
        }

        MainData.getIns().getRewardManager().removeReward(r);
        player.sendMessage(ChatColor.GREEN + "Reward has been removed");

    }
}
