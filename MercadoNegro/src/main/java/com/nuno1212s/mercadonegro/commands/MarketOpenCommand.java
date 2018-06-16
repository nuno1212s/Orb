package com.nuno1212s.mercadonegro.commands;

import com.nuno1212s.mercadonegro.main.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MarketOpenCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "THis command is for players only");
            return true;
        }

        ((Player) commandSender).openInventory(Main.getIns().getInventoryManager().getMainInventory());

        return true;

    }
}
