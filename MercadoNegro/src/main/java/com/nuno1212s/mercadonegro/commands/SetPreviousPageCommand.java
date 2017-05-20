package com.nuno1212s.mercadonegro.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.mercadonegro.main.Main;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Set the next page item command
 */
public class SetPreviousPageCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"setpreviouspage"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/market setpreviouspage <slots to last slot>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("market.setpreviouspage")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(player);
            return;
        }

        if (args.length < 2) {
            player.sendMessage(usage());
            return;
        }

        int slot;

        try {
            slot = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Slot must be a number");
            return;
        }

        ItemStack item = player.getItemInHand();
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You can't set the item to null!");
            return;
        }

        Main.getIns().getInventoryManager().setPreviousPageItem(slot, item);
        player.sendMessage(ChatColor.GREEN + "Previous page item has been set.");

    }
}
