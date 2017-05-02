package com.nuno1212s.classes.commands;

import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Handles class editing items
 */
public class ClassEditItemsCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"edititems"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/classes edititems <className>";
    }

    @Override
    public void execute(Player player, String[] args) {

    }
}
