package com.nuno1212s.mercado.commands;

import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventories.InventoryItem;
import com.nuno1212s.mercado.main.Main;
import com.nuno1212s.mercado.marketmanager.Item;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class OpenDirectBuy implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length < 1) {
            return true;
        }

        if (!(commandSender instanceof Player)) {
            return true;

        }

        String itemID = args[0];

        Item i = Main.getIns().getMarketManager().getItem(itemID);

        InventoryData confirmInventoryData = Main.getIns().getMarketManager().getConfirmInventoryData();

        Inventory confirmInventory = confirmInventoryData.buildInventory();

        InventoryItem show_item = confirmInventoryData.getItemWithFlag("SHOW_ITEM");

        if (show_item != null)
            confirmInventory.setItem(show_item.getSlot(), i.getDisplayItem());

        ((Player) commandSender).openInventory(confirmInventory);

        return true;
    }
}
