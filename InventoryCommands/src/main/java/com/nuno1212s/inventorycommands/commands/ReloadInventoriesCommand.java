package com.nuno1212s.inventorycommands.commands;

import com.nuno1212s.inventorycommands.InventoryMain;
import com.nuno1212s.main.MainData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadInventoriesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (commandSender.hasPermission("reloadInventories")) {

            InventoryMain.getIns().getCommandManager().reloadCommands();
            InventoryMain.getIns().getInventoryManager().reload();

            MainData.getIns().getMessageManager().getMessage("RELOADED_COMMANDS").sendTo(commandSender);

        }

        return true;
    }
}
