package com.nuno1212s.minas.commands;

import com.nuno1212s.minas.main.Main;
import com.nuno1212s.minas.minemanager.Mine;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Create a mine command
 */
public class MineCreateCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"create"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/mine create <mineID> <displayName> <timeInSeconds>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(usage());
            return;
        }
        String mineID = args[1], displayName = ChatColor.translateAlternateColorCodes('&', args[2]);
        long timeInSeconds = Long.parseLong(args[3]);

        if (Main.getIns().getMineManager().getMineByID(mineID) != null) {
            player.sendMessage(ChatColor.RED + "There is already a mine with that ID");
            return;
        }

        Mine m = new Mine(mineID, displayName, timeInSeconds * 1000, new HashMap<>(), null, null, null);
        Main.getIns().getMineManager().addMine(m);
        player.sendMessage(ChatColor.RED + "Created a mine with the id " + ChatColor.GRAY + mineID);
    }
}
