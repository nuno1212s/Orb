package com.nuno1212s.npcinbox.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.npcinbox.commands.chatcommands.CancelCommand;
import com.nuno1212s.npcinbox.commands.chatcommands.CleanCommand;
import com.nuno1212s.npcinbox.commands.chatcommands.FinishCommand;
import com.nuno1212s.npcinbox.commands.chatcommands.RLastCommand;
import com.nuno1212s.npcinbox.commands.entitycommands.RegisterEntityCommand;
import com.nuno1212s.npcinbox.commands.entitycommands.UnregisterEntityCommand;
import com.nuno1212s.npcinbox.main.Main;
import com.nuno1212s.util.CommandUtil.CommandManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Rewards command manager
 */
public class RewardsCommand extends CommandManager {

    public RewardsCommand() {
        super();
        addCommand(new CreateRewardCommand());
        addCommand(new CancelCommand());
        addCommand(new CleanCommand());
        addCommand(new FinishCommand());
        addCommand(new RLastCommand());
        addCommand(new OpenRewardInventoryCommand());
        addCommand(new RegisterEntityCommand());
        addCommand(new UnregisterEntityCommand());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender.hasPermission("rewards.command")) {
            return super.onCommand(commandSender, command, s, args);
        } else {
            Player player = (Player) commandSender;
            player.openInventory(Main.getIns().getInventoryManager().buildRewardInventoryForPlayer(MainData.getIns().getPlayerManager().getPlayer(player.getUniqueId())));
            return true;
        }
    }
}
