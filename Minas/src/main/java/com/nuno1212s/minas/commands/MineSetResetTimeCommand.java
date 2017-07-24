package com.nuno1212s.minas.commands;

import com.nuno1212s.minas.main.Main;
import com.nuno1212s.minas.minemanager.Mine;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Set the reset interval of the mine
 */
public class MineSetResetTimeCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"setresettime"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/mine setresettime <mineID> <timeInSeconds>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(usage());
            return;
        }

        Mine m = Main.getIns().getMineManager().getMineByID(args[1]);

        if (m == null) {
            player.sendMessage(ChatColor.RED + "There is no mine with that ID");
            return;
        }

        long time;

        try {
            time = Long.parseLong(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "The time must be a number");
            return;
        }

        m.setResetTimeMillis(time * 1000);
        player.sendMessage(ChatColor.GREEN + "Time has been set for the mine.");

    }
}
