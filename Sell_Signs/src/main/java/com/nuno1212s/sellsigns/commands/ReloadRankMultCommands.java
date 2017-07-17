package com.nuno1212s.sellsigns.commands;

import com.nuno1212s.sellsigns.main.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Reload rank multipliers
 */
public class ReloadRankMultCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.hasPermission("sellsigns.reloadranks")) {
            Main.getIns().reloadRankMult();
            commandSender.sendMessage(ChatColor.RED + "Reload rank multipliers.");
        }
        return true;
    }
}
