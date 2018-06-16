package com.nuno1212s.classes.commands;

import com.nuno1212s.classes.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Classes command
 */
public class ClassesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }

        Player player = (Player) commandSender;

        player.openInventory(Main.getIns().getKitManager().buildInventory(player));

        return true;
    }
}
