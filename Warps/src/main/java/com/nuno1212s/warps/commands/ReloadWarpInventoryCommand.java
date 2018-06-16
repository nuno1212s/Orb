package com.nuno1212s.warps.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.warps.main.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by COMP on 28/07/2017.
 */
public class ReloadWarpInventoryCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.hasPermission("warps.reloadWarpInventory")) {
            Main.getIns().getInventoryManager().reloadInventories();
            commandSender.sendMessage(ChatColor.RED + "Inventories reloaded");
            return true;
        }

        MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);

        return true;
    }
}
