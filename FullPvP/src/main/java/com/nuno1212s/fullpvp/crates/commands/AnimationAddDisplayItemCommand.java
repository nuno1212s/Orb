package com.nuno1212s.fullpvp.crates.commands;

import com.nuno1212s.fullpvp.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Add display item to animations command
 */
public class AnimationAddDisplayItemCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"addDisplayItem", "adp"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/crate addDisplayItem";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("crate.adp")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(player);
            return;
        }

        if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You must have an item in your hand");
            return;
        }

        Main.getIns().getCrateManager().getAnimationManager().addDisplayItem(player.getItemInHand());
        player.sendMessage(ChatColor.RED + "The show item has been added");
    }
}
