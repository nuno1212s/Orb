package com.nuno1212s.npcinbox.inventories;

import com.nuno1212s.rewards.Reward;
import com.nuno1212s.util.ItemUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles building inventories
 */
public class InventoryBuilder {

    private static final ItemStack confirm = new ItemStack(Material.WOOL, 1, (short) 5),
            deny = new ItemStack(Material.WOOL, 1, (short) 14);

    @Getter
    private Reward unfinishedReward;

    @Getter
    private Inventory inventory;

    public InventoryBuilder(Reward unfinishedReward) {
        this.unfinishedReward = unfinishedReward;
        this.inventory = Bukkit.getServer().createInventory(null, 36, ChatColor.RED + "Rewards Inventory");
        this.inventory.setItem(35, deny);
        this.inventory.setItem(34, confirm);
    }

    public static boolean isConfirm(ItemStack item) {
        return item.isSimilar(confirm);
    }

    public static boolean isDeny(ItemStack item) {
        return item.isSimilar(deny);
    }

    public Reward buildReward() {
        List<ItemStack> items = new ArrayList<>();

        ItemStack[] contents = inventory.getContents();
        for (ItemStack item : contents) {
            if (item == null || item.getType() == Material.AIR || item.getType() == Material.WOOL) {
                continue;
            }
            items.add(item);
        }

        unfinishedReward.setReward(items);

        return unfinishedReward;
    }


}
