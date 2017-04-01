package com.nuno1212s.hub.commands;

import com.nuno1212s.hub.messagemanager.Messages;
import com.nuno1212s.hub.utils.SpawnManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to perform this command.");
            return true;
        }

        Player p = (Player) commandSender;

        if (s.equalsIgnoreCase("spawn")) {

            p.teleport(SpawnManager.getIns().spawnLocation);
            p.sendMessage(Messages.getIns().getMessage("TeleportToSpawn", "&aYou were teleported to spawn."));

        }

        if (s.equalsIgnoreCase("setspawn")) {

            if (!p.hasPermission("novus.hub.command.setspawn")) {
                p.sendMessage(ChatColor.RED + "You don't have the permission to do this.");
                return true;
            }

            SpawnManager.getIns().setSpawnLocation(p.getLocation());
            p.sendMessage(ChatColor.GREEN + "You defined the location of spawn.");

        }

        return false;

    }

}
