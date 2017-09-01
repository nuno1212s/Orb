package com.nuno1212s.npcinbox.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.rewards.Reward;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Create a reward
 */
public class CreateRewardCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{""};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/reward create <serverType> <type> <args>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("createreward")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(player);
            return;
        }

        if (args.length < 4) {
            player.sendMessage(usage());
            return;
        }

        String serverType = args[1];
        Reward.RewardType type;
        try {
            type = Reward.RewardType.valueOf(args[2]);
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Type not found: " + Arrays.asList(Reward.RewardType.values()));
            return;
        }

        switch (type) {
            case MESSAGE: {
                break;
            }
            case ITEM: {
                break;
            }
            case CASH: {
                try {
                    long cash = Long.parseLong(args[3]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Wrong args, when reward type is cash, arg must the the cash amount");
                    return;
                }
                break;
            }
            case SV_CRRCY: {
                try {
                    long coins = Long.parseLong(args[3]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Wrong args, when reward type is sv_crrcy, arg must the the currency amount");
                    return;
                }
                break;
            }
        }

    }
}
