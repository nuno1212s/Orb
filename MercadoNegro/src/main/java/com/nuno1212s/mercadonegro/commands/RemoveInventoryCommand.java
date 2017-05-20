package com.nuno1212s.mercadonegro.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.mercadonegro.inventories.CInventory;
import com.nuno1212s.mercadonegro.main.Main;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Remove inventory command
 */
public class RemoveInventoryCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"removeinventory"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/market removeinventory <page>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("market.removeinventory")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(player);
            return;
        }

        if (args.length < 2) {
            player.sendMessage(usage());
            return;
        }

        int page;

        try {
            page = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Page must be a number");
            return;
        }

        CInventory inventory = Main.getIns().getInventoryManager().getInventory(page);

        if (inventory == null) {
            player.sendMessage(ChatColor.RED + "There is no inventory with that page");
            return;
        }

        Main.getIns().getInventoryManager().removeInventory(page);
        player.sendMessage("Page has been removed.");
    }
}
