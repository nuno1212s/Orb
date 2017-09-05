package com.nuno1212s.npcinbox.inventories;

import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.rewards.bukkit.BukkitReward;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import com.nuno1212s.util.inventories.InventoryData;
import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manages players creating a reward
 */
public class InventoryManager {

    private Map<UUID, InventoryBuilder> inventories;

    @Getter
    private InventoryData mainInventory;

    public InventoryManager(Module m) {
        inventories = new HashMap<>();
        this.mainInventory = new InventoryData(m.getFile("mainInventory.json", true), null);
    }

    public Inventory buildRewardInventoryForPlayer(PlayerData data) {
        List<Integer> toClaim = data.getToClaim();

        Inventory inventory = mainInventory.buildInventory();

        int currentSlot = 0;
        for (Integer rewardID : toClaim) {
            BukkitReward reward = (BukkitReward) MainData.getIns().getRewardManager().getReward(rewardID);
            if (reward == null) {
                continue;
            }
            inventory.setItem(currentSlot++, registerItem(reward.getItem().clone(), reward.getId()));
            if (currentSlot >= mainInventory.getInventorySize()) {
                break;
            }
        }

        return inventory;
    }

    /**
     * Embed the rewardID into the given item
     *
     * @param item
     * @param rewardID
     * @return
     */
    private ItemStack registerItem(ItemStack item, int rewardID) {
        NBTCompound compound = new NBTCompound(item);
        compound.add("RewardID", rewardID);
        return compound.write(item);
    }

    /**
     * Get the embedded rewardID in the given item
     * Result of {@link #registerItem(ItemStack, int)}
     *
     * @param item
     * @return
     */
    public int getEmbeddedReward(ItemStack item) {
        NBTCompound compound = new NBTCompound(item);
        if (compound.getValues().containsKey("RewardID")) {
            return (int) compound.getValues().get("RewardID");
        }
        return 0;
    }

    public InventoryBuilder registerPlayer(UUID player, BukkitReward unfinished) {
        InventoryBuilder value = new InventoryBuilder(unfinished);
        inventories.put(player, value);
        return value;
    }

    public void unregisterPlayer(UUID player) {
        inventories.remove(player);
    }

    public InventoryBuilder getPlayerInventoryBuilder(UUID player) {
        return inventories.getOrDefault(player, null);
    }


}
