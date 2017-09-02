package com.nuno1212s.npcinbox.inventories;

import com.nuno1212s.rewards.Reward;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;

/**
 * Handles building inventories
 */
public class InventoryBuilder {

    @Getter
    private Reward unfinishedReward;

    @Getter
    private Inventory inventory;

    public InventoryBuilder(Reward unfinishedReward) {
        this.unfinishedReward = unfinishedReward;
        this.inventory = Bukkit.getServer().createInventory(null, 36, ChatColor.RED + "Rewards Inventory");
    }



}
