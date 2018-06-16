package com.nuno1212s.boosters.commands;

import com.nuno1212s.boosters.main.Main;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Opens the booster inventory
 */
public class OpenBoosterInventoryCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"open"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/boosters open - Open the boosters inventory";
    }

    @Override
    public void execute(Player player, String[] args) {
        player.openInventory(Main.getIns().getInventoryManager().buildLandingInventory());
    }
}
