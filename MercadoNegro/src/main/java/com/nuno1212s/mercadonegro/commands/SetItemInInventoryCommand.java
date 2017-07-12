package com.nuno1212s.mercadonegro.commands;

import com.nuno1212s.mercadonegro.inventories.CInventory;
import com.nuno1212s.mercadonegro.inventories.Item;
import com.nuno1212s.mercadonegro.main.Main;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Set the item in the specified inventorylisteners
 */
public class SetItemInInventoryCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"setitem"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/market setitem <page> <slot> <cost> <iscash>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("market.setitem")) {
            return;
        }

        if (args.length < 5) {
            player.sendMessage(this.usage());
            return;
        }

        int page, slot;
        long cost;

        try {
            page = Integer.parseInt(args[1]);
            slot = Integer.parseInt(args[2]);
            cost = Long.parseLong(args[3]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Page, slot and cost must be numbers.");
            return;
        }

        boolean isCash = Boolean.parseBoolean(args[4]);

        CInventory inventory = Main.getIns().getInventoryManager().getInventory(page);

        if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
            inventory.setItem(slot, null);
            player.sendMessage(ChatColor.RED + "You have removed the item from the chosen slot");
            return;
        }

        Item item = new Item(player.getItemInHand(), cost, isCash);
        inventory.setItem(slot, item);
        player.sendMessage(ChatColor.RED + "You have set the item in your hand to the selected slot");
    }
}
