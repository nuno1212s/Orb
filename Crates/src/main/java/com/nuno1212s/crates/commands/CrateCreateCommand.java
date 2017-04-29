package com.nuno1212s.crates.commands;

import com.google.common.collect.Sets;
import com.nuno1212s.crates.Crate;
import com.nuno1212s.crates.Main;
import com.nuno1212s.main.MainData;
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
        return ChatColor.RED + "/crate create <name> <displayName>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if(!player.hasPermission("crate.create")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(player);
            return;
        }

        if (args.length < 3) {
            player.sendMessage(this.usage());
            return;
        }

        if (Main.getIns().getCrateManager().getCrate(args[1]) != null) {
            player.sendMessage(ChatColor.RED + "A crate with that name already exists");
            return;
        }

        String displayName = ChatColor.translateAlternateColorCodes('&', args[2]);
        Crate c = new Crate(args[1], displayName, Sets.newHashSet(), true, 0);
        Main.getIns().getCrateManager().addCrate(c);
        player.sendMessage(ChatColor.RED + "The crate with the name " + args[1] + " and display name "
                + c.getDisplayName() + " has been created.");
    }
}
