package com.nuno1212s.mercado.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.mercado.main.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Market Command
 */
public class MarketCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            MainData.getIns().getMessageManager().getMessage("PLAYER_ONLY").sendTo(commandSender);
            return true;
        }
        ((Player) commandSender).openInventory(Main.getIns().getMarketManager().getLandingInventory());
        return false;
    }
}
