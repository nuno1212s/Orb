package com.nuno1212s.events.war.commands;

import com.nuno1212s.events.EventMain;
import com.nuno1212s.main.MainData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetFallbackLocationCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {

            commandSender.sendMessage(ChatColor.RED + "This command can only be executed by a player!");

            return true;
        }

        if (commandSender.hasPermission("events.setFallback")) {

            EventMain.getIns().getWarEvent().getHelper().setFallbackLocation(((Player) commandSender).getLocation());

            MainData.getIns().getMessageManager().getMessage("LOCATION_SET").sendTo(commandSender);

        } else {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);
        }

        return true;
    }
}
