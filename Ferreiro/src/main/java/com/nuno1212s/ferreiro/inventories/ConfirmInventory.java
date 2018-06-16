package com.nuno1212s.ferreiro.inventories;

import com.nuno1212s.util.Callback;
import com.nuno1212s.util.Pair;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

/**
 * Handles the creation of the confirmation inventory
 */
public class ConfirmInventory {

    @Getter
    private Inventory inv;

    @Getter
    private Callback c;

    public ConfirmInventory(Pair<Integer, Boolean> cost, ItemStack itemInHand, Callback c) {
        this.c = c;
        inv = Bukkit.getServer().createInventory(null, 27, ChatColor.RED + "Confirm the repair");
        inv.setItem(13, itemInHand);
        ItemStack confirmItem = new ItemStack(Material.WOOL, 1, (short) 5);
        ItemMeta itemMeta = confirmItem.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Confirma");
        itemMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Custo: " + ChatColor.YELLOW + cost.getKey() + " " + ChatColor.GRAY + (cost.getValue() ? "cash" : "coins")));
        confirmItem.setItemMeta(itemMeta);
        inv.setItem(11, confirmItem);
        ItemStack denyItem = new ItemStack(Material.WOOL, 1, (short) 14);
        ItemMeta itemMeta1 = denyItem.getItemMeta();
        itemMeta1.setDisplayName(ChatColor.RED + "Cancela");
        denyItem.setItemMeta(itemMeta1);
        inv.setItem(15, denyItem);
    }

}
