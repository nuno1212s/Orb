package com.nuno1212s.minas.commands;

import com.nuno1212s.minas.main.Main;
import com.nuno1212s.minas.minemanager.Mine;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Set the default TP
 */
public class MineSetDefaultTPCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"setdtp"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/mine setdtp <mineID>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(usage());
            return;
        }

        Mine m = Main.getIns().getMineManager().getMineByID(args[1]);
        if (m == null) {
            player.sendMessage(ChatColor.RED + "No mine by that name!");
            return;
        }

        m.setDefaultTP(player.getLocation());
        player.sendMessage(ChatColor.RED + "Default TP set");

    }
}
