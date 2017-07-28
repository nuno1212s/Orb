package com.nuno1212s.multipliers.commands;

import com.nuno1212s.multipliers.main.RankMultiplierMain;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Reload rank multipliers
 */
public class ReloadRankMultipliersCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.hasPermission("reloadrankmultipliers")) {
            RankMultiplierMain.getIns().getRankManager().reload();
            commandSender.sendMessage(ChatColor.RED + "Reloaded the rank multipliers");
            return true;
        }
        return false;
    }

}
