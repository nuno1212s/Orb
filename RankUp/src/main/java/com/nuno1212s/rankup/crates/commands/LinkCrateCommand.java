package com.nuno1212s.rankup.crates.commands;

import com.nuno1212s.rankup.crates.Crate;
import com.nuno1212s.rankup.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * handles link crates command
 */
public class LinkCrateCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"linkcrate"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/crate linkcrate <crateName>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("crate.link")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").send(player);
            return;
        }

        if (args.length < 2) {
            player.sendMessage(usage());
            return;
        }

        Block targetBlock = player.getTargetBlock((Set<Material>) null, 5);
        if (targetBlock == null) {
            player.sendMessage(ChatColor.RED + "You are not looking at a block!");
            return;
        }

        Crate c = Main.getIns().getCrateManager().getCrate(args[1]);

        if (c == null) {
            player.sendMessage(ChatColor.RED + "A crate with that name does not exist");
            return;
        }

        Main.getIns().getCrateManager().setCrateAtLocation(targetBlock.getLocation(), c);
        player.sendMessage(ChatColor.RED + "Chest linked.");

    }
}
