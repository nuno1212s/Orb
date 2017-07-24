package com.nuno1212s.minas.commands;

import com.nuno1212s.minas.main.Main;
import com.nuno1212s.minas.minemanager.Mine;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Set the mine's corner
 */
public class MineSetCornerCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"setcorner"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/mine setcorner <mineID> <corner>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(usage());
            return;
        }

        Mine m = Main.getIns().getMineManager().getMineByID(args[1]);

        if (m == null) {
            player.sendMessage(ChatColor.RED + "No mine by that name!");
            return;
        }

        int corner;

        try {
            corner = Integer.parseInt(args[2]);
            if (corner > 2 || corner < 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "The corner must be a number (1 or 2)");
            return;
        }

        Block targetBlock = player.getTargetBlock((Set<Material>) null, 5);

        if (targetBlock == null) {
            player.sendMessage(ChatColor.RED + "You are not looking at a block");
            return;
        }

        if (corner == 1) {
            m.setCorner1(targetBlock.getLocation());
            player.sendMessage(ChatColor.RED + "Set the corner 1 to the block you are looking at");
            if (m.getCorner1() != null && m.getCorner2() != null) {
                m.calculateLocations();
            }
        } else {
            m.setCorner2(targetBlock.getLocation());
            player.sendMessage(ChatColor.RED + "Set the corner 2 to the block you are looking at");
            if (m.getCorner1() != null && m.getCorner2() != null) {
                m.calculateLocations();
            }
        }

    }
}
