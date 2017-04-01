package com.nuno1212s.core.commandmanager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nuno1212s.core.events.PlayerChat;

public class chat implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to perform this command");
            return true;
        }

        Player p = (Player) commandSender;

        if (args.length < 1) {
            return true;
        }
        String sub = args[0];

        if (sub.equalsIgnoreCase("clear")) {
            if (!p.hasPermission("novus.core.command.chat.clear")) {
                p.sendMessage(ChatColor.RED + "You do not have permission to perform this action.");
                return true;
            }

            clearChat(p);
            return true;

        }

        if (sub.equalsIgnoreCase("clearall")) {
            if (!p.hasPermission("novus.core.command.chat.clearall")) {
                p.sendMessage(ChatColor.RED + "You do not have permission to perform this action.");
                return true;
            }

            for (Player o : Bukkit.getOnlinePlayers())
                clearChat(o);

            return true;

        }

        if (args.length == 1) {

            if (!p.hasPermission("novus.core.command.chat.toggle")) {
                p.sendMessage(ChatColor.RED + "You do not have permission to perform this action.");
                return true;
            }

            if (args[0].equalsIgnoreCase("on")) {
                PlayerChat.canChat = true;
                p.sendMessage(ChatColor.GREEN + "The chat was activated.");

                return true;
            }

            if (args[0].equalsIgnoreCase("off")) {
                PlayerChat.canChat = false;
                p.sendMessage(ChatColor.RED + "The chat was disabled.");

                return true;
            }
        }
        return false;
    }

    private void clearChat(Player p) {
        int n = 0;
        while (n < 100) {
            p.sendMessage(" ");
            n++;
        }
    }

}
