package com.nuno1212s.inventorycommands.commands;

import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventorycommands.InventoryMain;
import com.nuno1212s.main.MainData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdExecutor implements CommandExecutor {

    /**
     * Registers and executes the commands that connect to the inventories
     * @param commandSender
     * @param command
     * @param s
     * @param strings
     * @return
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
            if (commandSender instanceof Player) {

                if (InventoryMain.getIns().getCommandManager().hasInventory(command.getName())) {
                    InventoryData inventory = InventoryMain.getIns().getCommandManager().getInventory(command.getName());

                    if (inventory != null) {
                        ((Player) commandSender).openInventory(inventory.buildInventory((Player) commandSender));
                    } else {

                        commandSender.sendMessage(ChatColor.RED + "Este comando não tem inventário conectado.");

                    }

                } else {

                    commandSender.sendMessage(ChatColor.RED + "Este comando não tem inventário conectado.");

                }

            } else {
                commandSender.sendMessage(ChatColor.RED + "This command is for players only");
            }

            return true;
        }

}
