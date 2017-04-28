package com.nuno1212s.rankup.crates.commands;

import com.nuno1212s.rankup.crates.Crate;
import com.nuno1212s.rankup.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Crate test opening command
 */
public class CrateTestCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"test"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/crate test <crateName>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("crate.test")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(player);
            return;
        }

        if (args.length < 2) {
            player.sendMessage(this.usage());
            return;
        }

        String crateName = args[1];

        Crate crate = Main.getIns().getCrateManager().getCrate(crateName);
        if (crate == null) {
            player.sendMessage(ChatColor.RED + "A crate with that name does not exist");
            return;
        }

        crate.openCase(player);

    }
}
