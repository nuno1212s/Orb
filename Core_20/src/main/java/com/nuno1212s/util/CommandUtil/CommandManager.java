package com.nuno1212s.util.CommandUtil;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.command.CommandExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * Command manager
 */
public abstract class CommandManager implements CommandExecutor {

    @Getter(value = AccessLevel.PROTECTED)
    protected List<Command> commands;

    public CommandManager() {
        commands = new ArrayList<>();
    }

    protected void addCommand(Command command) {
        this.commands.add(command);
    }

    protected Command getCommand(String commandName) {
        for (Command command : commands) {
            for (String s : command.names()) {
                if (s.equalsIgnoreCase(commandName)) {
                    return command;
                }
            }
        }
        return null;
    }

}
