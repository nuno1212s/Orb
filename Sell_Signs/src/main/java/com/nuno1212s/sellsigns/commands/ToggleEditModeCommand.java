package com.nuno1212s.sellsigns.commands;

import com.nuno1212s.sellsigns.main.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles toggling edit mode
 */
public class ToggleEditModeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            if (commandSender.hasPermission("sellsigns.toggleedit")) {
                Player p = (Player) commandSender;
                if (Main.getIns().getSignManager().isEditing(p.getUniqueId())) {
                    Main.getIns().getSignManager().removeFromEditing(p.getUniqueId());
                    commandSender.sendMessage(ChatColor.RED + "Removed from editing mode");
                } else {
                    Main.getIns().getSignManager().addToEditing(p.getUniqueId());
                    commandSender.sendMessage(ChatColor.GREEN + "Added to editing mode");
                }
            }
        }
        return true;
    }
}
