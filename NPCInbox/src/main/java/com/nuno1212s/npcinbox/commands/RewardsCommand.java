package com.nuno1212s.npcinbox.commands;

import com.nuno1212s.npcinbox.commands.chatcommands.CancelCommand;
import com.nuno1212s.npcinbox.commands.chatcommands.CleanCommand;
import com.nuno1212s.npcinbox.commands.chatcommands.FinishCommand;
import com.nuno1212s.npcinbox.commands.chatcommands.RLastCommand;
import com.nuno1212s.util.CommandUtil.CommandManager;

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
    }

}
