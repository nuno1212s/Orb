package com.nuno1212s.minas.commands;

import com.nuno1212s.minas.main.Main;
import com.nuno1212s.minas.minemanager.Mine;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Remove mine command
 */
public class RemoveMineCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"removemine"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/mine removemine <mineID>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(usage());
            return;
        }

        Mine m = Main.getIns().getMineManager().getMineByID(args[1]);

        if (m == null) {
            player.sendMessage(ChatColor.RED + "There is no mine with that ID");
            return;
        }

        Main.getIns().getMineManager().removeMine(m);
        player.sendMessage(ChatColor.GREEN + "Mine has been removed");

    }
}
