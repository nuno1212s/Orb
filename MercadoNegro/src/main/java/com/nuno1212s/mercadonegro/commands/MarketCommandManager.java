package com.nuno1212s.mercadonegro.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.mercadonegro.main.Main;
import com.nuno1212s.util.CommandUtil.CommandManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles market commands
 */
public class MarketCommandManager extends CommandManager {

    public MarketCommandManager() {
        super();

        addCommand(new CreateInventoryCommand());
        addCommand(new SetItemInInventoryCommand());
        addCommand(new RemoveInventoryCommand());
        addCommand(new SetNextPageCommand());
        addCommand(new SetPreviousPageCommand());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.hasPermission("market")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);
            return true;
        }
        if (args.length == 0) {
            ((Player) commandSender).openInventory(Main.getIns().getInventoryManager().getStartingInventory());
            return true;
        }

        com.nuno1212s.util.CommandUtil.Command c = getCommand(args[0]);

        if (c != null) {
            c.execute((Player) commandSender, args);
        } else {
            commandSender.sendMessage("");
            this.getCommands().forEach(cmd -> commandSender.sendMessage(cmd.usage()));
            commandSender.sendMessage("");
        }

        return true;
    }
}
