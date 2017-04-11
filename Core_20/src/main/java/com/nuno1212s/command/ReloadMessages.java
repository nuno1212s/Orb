package com.nuno1212s.command;

import com.nuno1212s.main.MainData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Reload messages command
 */
public class ReloadMessages implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.hasPermission("commands.reloadmessages") || commandSender.isOp()) {
            MainData.getIns().getMessageManager().reloadMessages();
            commandSender.sendMessage(ChatColor.RED + "Messages have been reloaded");
            return true;
        }
        return false;
    }
}
