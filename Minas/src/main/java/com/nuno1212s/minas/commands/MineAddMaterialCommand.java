package com.nuno1212s.minas.commands;

import com.nuno1212s.minas.main.Main;
import com.nuno1212s.minas.minemanager.Mine;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Add material to the mine
 */
public class MineAddMaterialCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"addmaterial"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/mine addmaterial <mineID> <prob> <materialID>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(usage());
            return;
        }

        Mine m = Main.getIns().getMineManager().getMineByID(args[1]);

        if (m == null) {
            player.sendMessage(ChatColor.RED + "No mine by that name!");
            return;
        }

        int prob, materialID;

        try {
            prob = Integer.parseInt(args[2]);
            materialID = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "prob and materialID must be numbers");
            return;
        }

        if (Material.getMaterial(materialID) == null) {
            player.sendMessage(ChatColor.RED + "Material not found");
            return;
        }

        m.addMaterial(prob, Material.getMaterial(materialID));

        player.sendMessage(ChatColor.RED + "Added material to the mine");

    }
}
