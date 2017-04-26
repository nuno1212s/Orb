package com.nuno1212s.fullpvp.crates.commands;

import com.nuno1212s.fullpvp.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Handles the set default key item command
 */
public class SetDefaultKeyItemCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"setdefaultkeyitem"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/crate setdefaultkeyitem";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("crate.setdefaultkeyitem")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(player);
            return;
        }

        ItemStack item = player.getItemInHand().clone();

        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "Tens de ter um item na mão");
            return;
        }

        Main.getIns().getCrateManager().setDefaultKeyItem(item);
        player.sendMessage(ChatColor.RED + "The default item has been set to the item you are holding");

    }
}
