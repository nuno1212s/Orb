package com.nuno1212s.punishments.inventories;

import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.inventories.InventoryData;
import lombok.Getter;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.*;

public class InventoryManager {

    private Map<UUID, UUID> playerTarget;

    public InventoryManager(Module m) {
        this.playerTarget = new WeakHashMap<>();

        File dataFolder = new File(m.getDataFolder() + File.separator + "Inventories" + File.separator);

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        File[] files = dataFolder.listFiles();

        for (File file : files) {
            new PInventory(file);
        }

    }

    /**
     * Get the main inventory
     *
     * @return
     */
    public Inventory getMainInventory() {
        return MainData.getIns().getInventoryManager().getInventory("punishMainInventory").buildInventory();
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

    public void removeTargetForPlayer(UUID player) {
        this.playerTarget.remove(player);
    }

}
