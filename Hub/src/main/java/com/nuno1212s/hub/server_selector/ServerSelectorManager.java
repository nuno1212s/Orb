package com.nuno1212s.hub.server_selector;

import com.nuno1212s.hub.util.HInventoryData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.util.inventories.InventoryData;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Inventory selecting manager
 */
public class ServerSelectorManager {

    private List<HInventoryData> inventories;

    public ServerSelectorManager(Module m) {
        inventories = new ArrayList<>();

        File dataFolder = new File(m.getDataFolder() + File.separator + "Inventories" + File.separator);

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        File[] files = dataFolder.listFiles();

        for (File file : files) {
            inventories.add(new HInventoryData(file.getName().replace(".json", ""), file));
        }

    }

    /**
     * Get the inventory data for an inventory
     *
     * @param inventoryID
     * @return
     */
    public InventoryData getInventoryData(String inventoryID) {
        for (HInventoryData inventory : inventories) {
            if (inventory.getInventoryID().equalsIgnoreCase(inventoryID)) {
                return inventory;
            }
        }

        return null;
    }

    /**
     * Get the inventory data with the inventory name
     *
     * @param inventoryName
     * @return
     */
    public InventoryData getInventoryDataByName(String inventoryName) {
        for (HInventoryData inventory : inventories) {
            if (inventory.getInventoryName().equalsIgnoreCase(inventoryName)) {
                return inventory;
            }
        }
        return null;
    }

    /**
     * Get the inventory data
     *
     * @param inventoryID
     * @return
     */
    public Inventory getInventory(String inventoryID) {
        InventoryData inventoryData = getInventoryData(inventoryID);

        if (inventoryData == null) {
            return null;
        }

        return inventoryData.buildInventory();
    }

    /**
     * Get the main inventory
     *
     * @return
     */
    public Inventory getMainInventory() {
        return getInventory("landingInventory");
    }

    /**
     *
     * @param inventoryName
     * @return
     */
    public Inventory getInventoryByName(String inventoryName) {
        InventoryData inventoryDataByName = getInventoryDataByName(inventoryName);

        if (inventoryDataByName == null) {
            return null;
        }

        return inventoryDataByName.buildInventory();
    }

}
