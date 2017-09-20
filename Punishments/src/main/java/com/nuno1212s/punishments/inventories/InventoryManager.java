package com.nuno1212s.punishments.inventories;

import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.punishments.util.PInventoryItem;
import com.nuno1212s.util.inventories.InventoryData;
import lombok.Getter;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.*;

public class InventoryManager {

    @Getter
    private List<InventoryData> inventories;

    private Map<UUID, UUID> playerTarget;

    public InventoryManager(Module m) {
        this.inventories = new ArrayList<>();
        this.playerTarget = new WeakHashMap<>();

        File dataFolder = new File(m.getDataFolder() + File.separator + "Inventories" + File.separator);

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        File[] files = dataFolder.listFiles();
        for (File file : files) {
            this.inventories.add(new InventoryData(file, PInventoryItem.class));
        }

    }

    /**
     * Get the inventory data with the given inventory ID
     *
     * @param inventoryID
     * @return
     */
    public InventoryData getInventoryWithID(String inventoryID) {
        for (InventoryData inventory : inventories) {
            if (inventory.getInventoryID().equalsIgnoreCase(inventoryID)) {
                return inventory;
            }
        }
        return null;
    }

    /**
     * Get the inventory data for a inventory
     * @param inventory
     * @return
     */
    public InventoryData getInventoryFromInventory(Inventory inventory) {
        for (InventoryData inventoryData : inventories) {
            if (inventoryData.equals(inventory)) {
                return inventoryData;
            }
        }
        return null;
    }

    /**
     * Get the inventory instance with the given inventory ID
     *
     * @param inventoryID
     * @return
     */
    public Inventory getInventory(String inventoryID) {
        return getInventoryWithID(inventoryID).buildInventory();
    }

    /**
     * Get the main inventory
     *
     * @return
     */
    public Inventory getMainInventory() {
        return getInventory("mainInventory");
    }

    /**
     * Get the target for a specific player
     *
     * @param player
     * @return
     */
    public UUID getTargetForPlayer(UUID player) {
        return this.playerTarget.get(player);
    }

    /**
     * Set the target for a specified target
     *
     * @param player
     * @param target
     */
    public void setPlayerToTarget(UUID player, UUID target) {
        this.playerTarget.put(player, target);
    }

}
