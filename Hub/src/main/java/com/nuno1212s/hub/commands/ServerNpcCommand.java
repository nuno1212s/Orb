package com.nuno1212s.hub.commands;

import com.nuno1212s.hub.servermanager.NovusServer;
import com.nuno1212s.hub.servermanager.ServerManager;
import com.nuno1212s.hub.utils.ConfigUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ServerNpcCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to perform this command.");
            return true;
        }

        Player p = (Player) commandSender;

        if (!p.hasPermission("novus.hub.command.servernpc")) {
            p.sendMessage(ChatColor.RED + "You don't have the permission to do this.");
            return true;
        }

        if (args.length < 1) {

            p.sendMessage(ChatColor.AQUA + "Commands: ");
            p.sendMessage(ChatColor.DARK_AQUA + "- /servernpc setspawnlocation <bungeeid>");

            return true;
        }

        if (args.length > 0) {

            String subcommand = args[0];

            if (subcommand.equalsIgnoreCase("setspawnlocation")) {

                if (args.length > 1) {
                    String bungeeId = args[1];
                    NovusServer ns = ServerManager.getIns().getServerByBungeeID(bungeeId);
                    if (ns == null) {
                        p.sendMessage(ChatColor.RED + "Invalid bungee id.");
                        return true;
                    }
                    Location l = p.getLocation();
                    ConfigUtils.getIns().setLocation("Servers." + ns.getConfiguratioName() + ".location", l, ServerManager.getIns().fc);
                    ServerManager.getIns().saveConfig();
                    p.sendMessage(ChatColor.GREEN + "Location saved. Restart server to load changes.");
                } else {
                    p.sendMessage(ChatColor.AQUA + "Use: " + ChatColor.DARK_AQUA + "/hnpc setspawnlocation <bungeeid>");
                    return true;
                }
            } else {
                p.sendMessage(ChatColor.RED + "Subcommand not found.");
            }
        }
        return false;
    }

}
