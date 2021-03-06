package com.nuno1212s.crates.commands;

import com.nuno1212s.crates.crates.Crate;
import com.nuno1212s.crates.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Buy crate key command
 */
public class BuyCrateKeyCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"buycrate"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/crate buycrate <crateName>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(this.usage());
            return;
        }

        Crate c = Main.getIns().getCrateManager().getCrate(args[1]);

        if (c == null) {
            MainData.getIns().getMessageManager().getMessage("NO_CRATE_WITH_THAT_NAME").sendTo(player);
            return;
        }

        c.buyKey(player);

    }
}
