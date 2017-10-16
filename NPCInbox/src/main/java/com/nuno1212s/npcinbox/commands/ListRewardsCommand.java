package com.nuno1212s.npcinbox.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.rewards.Reward;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ListRewardsCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"list"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/reward list - List the rewards";
    }

    @Override
    public void execute(Player player, String[] args) {
        for (Reward reward : MainData.getIns().getRewardManager().getRewards()) {
            player.sendMessage(ChatColor.RED + reward.toString());
        }
    }
}
