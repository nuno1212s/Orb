package com.nuno1212s.displays.commands;

import com.nuno1212s.displays.Main;
import com.nuno1212s.main.MainData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles chat control commands
 */
public class ChatControlCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender.hasPermission("chat.control")) {
            if (args.length < 1) {
                commandSender.sendMessage(ChatColor.RED + "/chat on/off");
                commandSender.sendMessage(ChatColor.RED + "/chat clear");
                return true;
            }

            if (args[0].equalsIgnoreCase("on")) {
                if (commandSender.hasPermission("chat.toggle")) {
                    Main.getIns().getChatManager().setChatActivated(true);
                    commandSender.sendMessage(ChatColor.RED + "Chat has been activated");
                    Bukkit.broadcastMessage(MainData.getIns().getMessageManager().getMessage("CHAT_ACTIVATED").toString());
                } else {
                    MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);
                }
            } else if (args[0].equalsIgnoreCase("off")) {
                if (commandSender.hasPermission("chat.toggle")) {
                    Main.getIns().getChatManager().setChatActivated(false);
                    commandSender.sendMessage(ChatColor.RED + "Chat has been deactivated");
                    Bukkit.broadcastMessage(MainData.getIns().getMessageManager().getMessage("CHAT_DEACTIVATED").toString());
                }
            } else if (args[0].equalsIgnoreCase("clear")) {
                for (int i = 0; i < 50; i++) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(" ");
                    }
                }
            }

        } else {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);
        }
        return true;
    }
}
