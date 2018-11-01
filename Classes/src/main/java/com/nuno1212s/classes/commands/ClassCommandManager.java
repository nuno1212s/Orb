package com.nuno1212s.classes.commands;

import com.nuno1212s.util.CommandUtil.commandexecutors.CommandManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Manages class commands
 */
public class ClassCommandManager extends CommandManager {

    public ClassCommandManager() {
        super();
        addCommand(new ClassCreateCommand());
        addCommand(new ClassEditItemsCommand());
        addCommand(new ClassGetCommand());
        addCommand(new ClassDeleteCommand());
        addCommand(new ClassSeeItemsCommand());
        addCommand(new ReloadDisplayCommand());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 0 && commandSender.hasPermission("classes.seeCommands")) {
            commandSender.sendMessage("");
            this.getCommands().forEach(cmd -> commandSender.sendMessage(cmd.usage()));
            commandSender.sendMessage("");
            return true;
        }

        com.nuno1212s.util.CommandUtil.Command c = getCommand(args[0]);

        if (c != null) {
            c.execute((Player) commandSender, args);
        } else {
            if (commandSender.hasPermission("classes.seeCommands")) {
                commandSender.sendMessage("");
                this.getCommands().forEach(cmd -> commandSender.sendMessage(cmd.usage()));
                commandSender.sendMessage("");
            }
        }
        return true;
    }
}
