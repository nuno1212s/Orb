package com.nuno1212s.classes.commands;

import com.nuno1212s.classes.Main;
import com.nuno1212s.classes.classmanager.Kit;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Class set display command
 */
public class ClassSetDisplayCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"setdisplayitem"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/class setdisplayitem <classname>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("class.setdisplay")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(player);
            return;
        }

        if (args.length < 2) {
            player.sendMessage(usage());
            return;
        }

        if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You don't have an item in your hand.");
            return;
        }

        Kit k = Main.getIns().getKitManager().getKit(args[1]);

        if (k == null) {
            player.sendMessage(ChatColor.RED + "There is no kit with that name.");
            return;
        }

        k.setDisplayItem(player.getItemInHand().clone());
        player.sendMessage(ChatColor.RED + "Display item has been changed for the kit.");

    }
}
