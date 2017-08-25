package com.nuno1212s.spawners.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.spawners.main.Main;
import com.nuno1212s.spawners.rewardhandler.RewardManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Toggles the instant reward for killing mobs
 */
public class InstantRewardCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender.hasPermission("activateInstantReward")) {
            RewardManager rewardManager = Main.getIns().getRewardManager();
            rewardManager.setInstantReward(!rewardManager.isInstantReward());

            MainData.getIns().getMessageManager().getMessage("INSTANT_REWARD_SET")
                    .format("%value%", String.valueOf(rewardManager.isInstantReward())).sendTo(commandSender);
        } else {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);
        }
        return true;
    }
}
