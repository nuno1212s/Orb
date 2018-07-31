package com.nuno1212s.rankup.skillvisualizer;

import com.nuno1212s.rankup.main.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkillCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!(commandSender instanceof Player)) {

            commandSender.sendMessage(ChatColor.RED + "This is a player only command.");

            return true;
        }

        Main.getIns().getSkillVisualizer().getInventory((Player) commandSender)
                .thenAccept((inv) -> {
                    if (((Player) commandSender).isOnline())
                        ((Player) commandSender).openInventory(inv);
                });

        return true;
    }
}
