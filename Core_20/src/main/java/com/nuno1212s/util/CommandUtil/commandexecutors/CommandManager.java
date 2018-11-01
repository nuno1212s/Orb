package com.nuno1212s.util.CommandUtil.commandexecutors;

import com.nuno1212s.util.CommandUtil.Command;
import com.nuno1212s.util.CommandUtil.Commands;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Command manager
 */
public abstract class CommandManager implements CommandExecutor, Commands {

    @Getter(value = AccessLevel.PROTECTED)
    protected List<Command> commands;

    public CommandManager() {
        commands = new ArrayList<>();
    }

    public void addCommand(Command command) {
        this.commands.add(command);
    }

    public Command getCommand(String commandName) {
        for (Command command : commands) {
            for (String s : command.names()) {
                if (s.equalsIgnoreCase(commandName)) {
                    return command;
                }
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] args) {
        if (args.length < 1) {
            commands.forEach(c -> commandSender.sendMessage(c.usage()));
            return true;
        }

        Command subCommand = getCommand(args[0]);

        if (subCommand != null) {
            subCommand.execute((Player) commandSender, args);
        } else {
            commands.forEach(c -> commandSender.sendMessage(c.usage()));
        }

        return true;
    }
}
