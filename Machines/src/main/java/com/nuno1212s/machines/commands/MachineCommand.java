package com.nuno1212s.machines.commands;

import com.nuno1212s.machines.main.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MachineCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            ((Player) commandSender).openInventory(Main.getIns().getInventoryManager().getMainInventory().buildInventory());
        } else {
            commandSender.sendMessage(ChatColor.RED + "You can only do this when you are a player");
        }

        return false;
    }
}
