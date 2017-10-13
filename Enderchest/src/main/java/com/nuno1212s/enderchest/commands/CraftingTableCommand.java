package com.nuno1212s.enderchest.commands;

import com.nuno1212s.main.MainData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class CraftingTableCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("craftingTable")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);
            return true;
        }

        Player p = (Player) commandSender;

        p.openInventory(Bukkit.getServer().createInventory(null, InventoryType.WORKBENCH));

        return true;
    }
}
