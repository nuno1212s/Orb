package com.nuno1212s.npcinbox.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.npcinbox.inventories.InventoryBuilder;
import com.nuno1212s.npcinbox.main.Main;
import com.nuno1212s.rewards.bukkit.BukkitReward;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Create a reward
 */
public class CreateRewardCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"create"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/reward create <serverType> <type> <isDefault> <args>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("rewards.createreward")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(player);
            return;
        }

        if (args.length < 5) {
            player.sendMessage(usage());
            return;
        }

        if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You must have the reward item in your hand");
            return;
        }

        String serverType = args[1].equalsIgnoreCase("CURRENT") ? MainData.getIns().getServerManager().getServerType() : args[1];
        BukkitReward.RewardType type;
        boolean isDefault = Boolean.parseBoolean(args[3]);
        try {
            type = BukkitReward.RewardType.valueOf(args[2]);
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Type not found: " + Arrays.asList(BukkitReward.RewardType.values()));
            return;
        }

        BukkitReward r = new BukkitReward(0, type, isDefault, serverType, player.getItemInHand().clone(), null);

        switch (type) {
            case MESSAGE: {
                Main.getIns().getChatManager().registerPlayer(player.getUniqueId(), r);
                player.sendMessage(ChatColor.GREEN + "Type the messages in the chat:");
                player.sendMessage(ChatColor.RED + "Use /reward cancel to cancel the reward creation");
                player.sendMessage(ChatColor.RED + "Use /reward clean to reset the messages");
                player.sendMessage(ChatColor.RED + "Use /reward rlast to remove the last message");
                player.sendMessage(ChatColor.GREEN + "Use /reward finish to create the reward");
                break;
            }
            case ITEM: {
                InventoryBuilder inventoryBuilder = Main.getIns().getInventoryManager().registerPlayer(player.getUniqueId(), r);
                player.openInventory(inventoryBuilder.getInventory());
                break;
            }
            case CASH: {
                try {
                    long cash = Long.parseLong(args[3]);
                    r.setReward(cash);
                    MainData.getIns().getRewardManager().createReward(r);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Wrong args, when reward type is cash, arg must the the cash amount");
                    return;
                }
                break;
            }
            case SV_CRRCY: {
                try {
                    long coins = Long.parseLong(args[3]);
                    r.setReward(coins);
                    MainData.getIns().getRewardManager().createReward(r);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Wrong args, when reward type is sv_crrcy, arg must the the currency amount");
                    return;
                }
                break;
            }
        }

    }
}
