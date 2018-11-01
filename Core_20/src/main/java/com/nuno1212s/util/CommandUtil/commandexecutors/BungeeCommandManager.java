package com.nuno1212s.util.CommandUtil.commandexecutors;

import com.nuno1212s.util.CommandUtil.Commands;
import lombok.AccessLevel;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import org.bukkit.entity.Player;

import java.util.List;

public class BungeeCommandManager extends Command implements Commands {

    @Getter(value = AccessLevel.PROTECTED)
    protected List<com.nuno1212s.util.CommandUtil.Command> commands;

    public BungeeCommandManager(String name) {
        super(name);
    }

    public void addCommand(com.nuno1212s.util.CommandUtil.Command command) {
        this.commands.add(command);
    }

    public com.nuno1212s.util.CommandUtil.Command getCommand(String commandName) {
        for (com.nuno1212s.util.CommandUtil.Command command : commands) {
            for (String s : command.names()) {
                if (s.equalsIgnoreCase(commandName)) {
                    return command;
                }
            }
        }
        return null;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        if (args.length < 1) {
            commands.forEach(c -> commandSender.sendMessage(TextComponent.fromLegacyText(c.usage())));

            return;
        }

        com.nuno1212s.util.CommandUtil.Command subCommand = getCommand(args[0]);

        if (subCommand != null) {
            subCommand.execute((Player) commandSender, args);
        } else {
            commands.forEach(c -> commandSender.sendMessage(TextComponent.fromLegacyText(c.usage())));
        }

    }
}
