package com.nuno1212s.punishments.commands;

import com.nuno1212s.punishments.main.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        ((Player) commandSender).openInventory(Main.getIns().getInventoryManager().getMainInventory());
        return false;
    }
}
