package com.nuno1212s.core.serverstatus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Set can save
 */
public class SetCanSave implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.isOp() || commandSender.hasPermission("permission.canSave")) {
            if (strings.length == 0) {
                commandSender.sendMessage("/setcansave <true/false>");
                return true;
            }
            ServerStatus.getIns().setCanSave(Boolean.parseBoolean(strings[0].toLowerCase()));
            ServerStatus.getIns().save();
            return true;
        } else {
            //NO PERM
        }
        return true;
    }
}


