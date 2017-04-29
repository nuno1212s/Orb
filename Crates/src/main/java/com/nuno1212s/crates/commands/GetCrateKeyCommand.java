package com.nuno1212s.crates.commands;

import com.nuno1212s.crates.Crate;
import com.nuno1212s.crates.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Get the crate key command
 */
public class GetCrateKeyCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"getcratekey"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/crate getcratekey <crate>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("crate.getcrate")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(player);
            return;
        }

        if (args.length < 2) {
            player.sendMessage(this.usage());
            return;
        }

        Crate c = Main.getIns().getCrateManager().getCrate(args[1]);

        if (c == null) {
            player.sendMessage(ChatColor.RED + "A crate with that name does not exist.");
            return;
        }

        player.getInventory().addItem(c.formatKeyItem());
        player.sendMessage(ChatColor.RED + "You have received the crate item.");

    }
}
