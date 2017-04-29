package com.nuno1212s.crates.commands;

import com.nuno1212s.crates.Crate;
import com.nuno1212s.crates.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Set the crate key prices
 */
public class SetCrateKeyCostCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"setcratekeycost"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/crate setcratekeycost <crate> <cost> <cash>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("crate.setkeycost")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(player);
            return;
        }

        if (args.length < 3) {
            player.sendMessage(usage());
            return;
        }

        Crate c = Main.getIns().getCrateManager().getCrate(args[1]);

        if (c == null) {
            player.sendMessage(ChatColor.RED + "A crate with that name does not exist.");
            return;
        }

        long cost;
        boolean cash;

        try {
            cost = Long.parseLong(args[2]);
            cash = Boolean.parseBoolean(args[3]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "The cost must be a number");
            return;
        }

        c.setCash(cash);
        c.setKeyCost(cost);

        player.sendMessage(ChatColor.RED + "The crate key cost has been set to " + String.valueOf(cost) + (cash ? ChatColor.GREEN + "cash" : ChatColor.GOLD + "coins"));

    }
}
