package com.nuno1212s.boosters.commands;

import com.nuno1212s.util.CommandUtil.CommandManager;

/**
 * Booster command manager
 */
public class BoosterCommandManager extends CommandManager {

    public BoosterCommandManager() {
        super();
        addCommand(new AddBoosterToPlayerCommand());
    }

}
