package com.nuno1212s.minas.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.util.CommandUtil.commandexecutors.CommandManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Mine commands
 */
public class MineCommands extends CommandManager {

    public MineCommands() {
        super();
        addCommand(new MineCreateCommand());
        addCommand(new MineAddMaterialCommand());
        addCommand(new MineSetCornerCommand());
        addCommand(new MineSetDefaultTPCommand());
        addCommand(new RemoveMineCommand());
        addCommand(new MineSetResetTimeCommand());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!commandSender.hasPermission("mines.edit")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);
            return true;
        }

        if (args.length < 1) {
            commandSender.sendMessage("");
            this.commands.forEach(cmd -> commandSender.sendMessage(cmd.usage()));
            commandSender.sendMessage("");
            return true;
        }

        com.nuno1212s.util.CommandUtil.Command c = getCommand(args[0]);
        if (c != null) {
            c.execute((Player) commandSender, args);
            return true;
        }

        commandSender.sendMessage("");
        this.commands.forEach(cmd -> commandSender.sendMessage(cmd.usage()));
        commandSender.sendMessage("");

        return true;
    }
}
