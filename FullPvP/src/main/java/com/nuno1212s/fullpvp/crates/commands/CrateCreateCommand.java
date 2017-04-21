package com.nuno1212s.fullpvp.crates.commands;

import com.google.common.collect.Lists;
import com.nuno1212s.fullpvp.crates.Crate;
import com.nuno1212s.fullpvp.main.Main;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Creates a crate
 */
public class CrateCreateCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"create"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/crate create <name>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(this.usage());
            return;
        }

        if (Main.getIns().getCrateManager().getCrate(args[1]) != null) {
            player.sendMessage(ChatColor.RED + "A crate with that name already exists");
            return;
        }

        Crate c = new Crate(args[1], Lists.newArrayList());
        Main.getIns().getCrateManager().addCrate(c);
        player.sendMessage(ChatColor.RED + "The crate with the name " + args[1] + " has been created.");
    }
}
