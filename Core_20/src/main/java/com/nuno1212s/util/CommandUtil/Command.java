package com.nuno1212s.util.CommandUtil;

import org.bukkit.entity.Player;

/**
 * Command interface
 */
public interface Command {

    String[] names();

    String usage();

    void execute(Player player, String[] args);

}
