package com.nuno1212s.warps.commands;

import com.nuno1212s.warps.main.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MinesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {
            return true;
        }

        ((Player) commandSender).openInventory(Main.getIns().getInventoryManager().getInventory("minas"));

        return true;
    }
}
