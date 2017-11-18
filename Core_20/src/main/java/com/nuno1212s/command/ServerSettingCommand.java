package com.nuno1212s.command;

import com.nuno1212s.main.MainData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Set server settings
 */
public class ServerSettingCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        /*
        /server setSVName <svname>
        /server setSVType <svtype>
         */

        if (!commandSender.hasPermission("serverSettings")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);
            return true;
        }

        if (args.length < 2) {
            commandSender.sendMessage("");
            commandSender.sendMessage("/server setSVName <svname>");
            commandSender.sendMessage("/server setSVType <svtype>");
            commandSender.sendMessage("");
            return true;
        }

        if (args[0].equalsIgnoreCase("setsvname")) {
            String serverName = args[1];
            MainData.getIns().getServerManager().setServerName(serverName);
            commandSender.sendMessage(ChatColor.RED + "The server name has been set to " + serverName);
        } else if (args[0].equalsIgnoreCase("setsvtype")) {
            String serverType = args[1];
            MainData.getIns().getServerManager().setServerType(serverType);
            commandSender.sendMessage(ChatColor.RED + "The server type has been set to " + serverType);
        } else {
            commandSender.sendMessage("");
            commandSender.sendMessage("/server setSVName <svname>");
            commandSender.sendMessage("/server setSVType <svtype>");
            commandSender.sendMessage("");
        }

        return true;
    }
}
