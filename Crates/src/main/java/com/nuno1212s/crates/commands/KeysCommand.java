package com.nuno1212s.crates.commands;

import com.nuno1212s.crates.Main;
import com.nuno1212s.crates.crates.Crate;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Keys command
 */
public class KeysCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!commandSender.hasPermission("givecratekey") && !commandSender.isOp()) {

            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);

            return true;

        }

        if (args.length < 3) {

            commandSender.sendMessage("Wrong usage");
            commandSender.sendMessage(command.getName() + " <player> <crate> <amount>");

            return true;
        }

        String playerName = args[0];

        PlayerData player = MainData.getIns().getPlayerManager().getPlayer(playerName);

        if (player == null || !player.isPlayerOnServer()) {

            MainData.getIns().getMessageManager().getMessage("PLAYER_NOT_ONLINE").sendTo(commandSender);

            return true;
        }

        Player playerInstance = player.getPlayerReference(Player.class);

        int keyAmount = Integer.parseInt(args[2]);

        if (keyAmount > 64) {
            MainData.getIns().getMessageManager().getMessage("TOO_MANY_KEYS").sendTo(commandSender);

            return true;
        }

        String crateName = args[1];

        Crate c = Main.getIns().getCrateManager().getCrate(crateName);

        if (c != null) {

            ItemStack itemStack = c.formatKeyItem();

            itemStack.setAmount(keyAmount);

            playerInstance.getInventory().addItem(itemStack);

            MainData.getIns().getMessageManager().getMessage("KEYS_ADDED").sendTo(commandSender);

            MainData.getIns().getMessageManager().getMessage("ADDED_KEYS")
                    .format("%keyAmount%", keyAmount)
                    .format("%crateName%", c.getDisplayName())
                    .sendTo(playerInstance);

        } else {

            MainData.getIns().getMessageManager().getMessage("CRATE_DOES_NOT_EXIST").sendTo(commandSender);

        }

        return true;
    }
}
