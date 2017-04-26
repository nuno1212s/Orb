package com.nuno1212s.fullpvp.crates.commands;

import com.nuno1212s.util.CommandUtil.CommandManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles crate command
 */
public class CrateCommandManager extends CommandManager {

    public CrateCommandManager() {
        super();

        addCommand(new CrateAddRewardCommand());
        addCommand(new CrateCreateCommand());
        addCommand(new AnimationAddDisplayItemCommand());
        addCommand(new CrateTestCommand());
        addCommand(new CrateRemoveRewardCommand());
        addCommand(new CrateRewardListCommand());
        addCommand(new LinkCrateCommand());

    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage("");
            this.getCommands().forEach(cmd -> commandSender.sendMessage(cmd.usage()));
            commandSender.sendMessage("");
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
