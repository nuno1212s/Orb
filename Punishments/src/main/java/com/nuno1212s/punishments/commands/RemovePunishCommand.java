package com.nuno1212s.punishments.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.Pair;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemovePunishCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.hasPermission("punishments.remove")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);
            return true;
        }

        if (args.length < 1) {
            commandSender.sendMessage(ChatColor.RED + "/unpunish <player>");
            return true;
        }

        Pair<PlayerData, Boolean> orLoadPlayer = MainData.getIns().getPlayerManager().getOrLoadPlayer(args[0]);

        if (orLoadPlayer.key() == null) {
            commandSender.sendMessage(ChatColor.RED + "Player has never joined the server!");
            return true;
        }

        PlayerData key = orLoadPlayer.key();

        key.setPunishment(null);

        if (orLoadPlayer.value()) {
            key.save((o) -> {
            });
        } else {
            MainData.getIns().getMessageManager().getMessage("REMOVED_PUNISHMENT").sendTo(key.getPlayerReference(Player.class));
        }

        commandSender.sendMessage(ChatColor.GREEN + "The players punishment as been removed");

        return true;
    }
}
