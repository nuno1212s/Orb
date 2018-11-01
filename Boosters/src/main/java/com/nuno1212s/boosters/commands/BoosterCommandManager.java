package com.nuno1212s.boosters.commands;

import com.nuno1212s.boosters.main.Main;
import com.nuno1212s.util.CommandUtil.commandexecutors.CommandManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Booster command manager
 */
public class BoosterCommandManager extends CommandManager {

    public BoosterCommandManager() {
        super();
        addCommand(new AddBoosterToPlayerCommand());
        addCommand(new OpenBoosterInventoryCommand());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (commandSender.hasPermission("boosters.admin") && args.length < 1) {
            this.commands.forEach((c) -> commandSender.sendMessage(c.usage()));
            return true;
        }

        if (args.length > 1 && commandSender.hasPermission("boosters.admin")) {
            com.nuno1212s.util.CommandUtil.Command command1 = getCommand(args[0]);
            if (command1 == null) {
                this.commands.forEach((c) -> commandSender.sendMessage(c.usage()));
            } else {
                command1.execute((Player) commandSender, args);
            }
            return true;
        }

        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            p.openInventory(Main.getIns().getInventoryManager().buildLandingInventory());
        }

        return true;
    }
}
