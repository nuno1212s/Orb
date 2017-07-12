package com.nuno1212s.mercadonegro.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.mercadonegro.inventories.CInventory;
import com.nuno1212s.mercadonegro.inventories.Item;
import com.nuno1212s.mercadonegro.main.Main;
import com.nuno1212s.util.CommandUtil.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Create inventorylisteners command
 */
public class CreateInventoryCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"ci", "createinventory"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/market createinventory <inventorySize> <inventoryName>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("market.createinventory")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(player);
            return;
        }
        if (args.length < 3) {
            player.sendMessage(usage());
            return;
        }

        StringBuilder inventoryName = new StringBuilder();

        for (int i = 2; i < args.length; i++) {
            if (i == 2) {
                inventoryName.append(args[i]);
            } else {
                inventoryName.append(" ");
                inventoryName.append(args[i]);
            }
        }

        String iN = ChatColor.translateAlternateColorCodes('&', inventoryName.toString());
        int inventorySize;

        try {
            inventorySize = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Inventory Size must be a number");
            return;
        }

        CInventory inventory = new CInventory(iN, inventorySize, new Item[inventorySize]);
        int page = Main.getIns().getInventoryManager().addInventory(inventory);
        player.sendMessage(ChatColor.GREEN + "You have successfully created the inventorylisteners. Page " + page);
    }
}
