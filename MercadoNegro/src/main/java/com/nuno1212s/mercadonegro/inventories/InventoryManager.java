package com.nuno1212s.mercadonegro.inventories;

import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.util.ItemUtils;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import com.nuno1212s.util.Pair;
import com.nuno1212s.util.inventories.InventoryData;
import com.nuno1212s.util.inventories.InventoryItem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages inventories
 */
public class InventoryManager {

    @Getter
    private List<CInventoryData> inventories;

    private InventoryData confirmInventory;

    public InventoryManager(Module m) {
        this.inventories = new ArrayList<>();

        File dataFolder = new File(m.getDataFolder() + File.separator + "Inventories" + File.separator);

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
            saveDefaultInventory(m);
        }

        confirmInventory = new CInventoryData(m.getFile("confirmInventory.json", true));

        for (File file : dataFolder.listFiles()) {
            this.inventories.add(new CInventoryData(file));
        }

        assert getInventory("mainInventory") != null;

    }

    /**
     * Saves the default main inventory
     * @param m
     */
    private void saveDefaultInventory(Module m) {
        m.getFile("Inventories" + File.separator + "mainInventory.json", true);
    }

    /**
     * Get the main black market inventory
     * @return
     */
    public Inventory getMainInventory() {
        return buildInventory("mainInventory");
    }

    /**
     * Get the confirm inventory
     * @return
     */
    public InventoryData getConfirmInventory() {
        return this.confirmInventory;
    }

    /**
     * Build inventory data
     *
     * @param toSell The item to sell to the player
     * @return
     */
    public Inventory buildConfirmInventory(CInventoryItem toSell) {
        InventoryData confirmInventory = getConfirmInventory();

        Inventory inventory = confirmInventory.buildInventory();

        InventoryItem item = confirmInventory.getItemWithFlag("ITEM");

        ItemStack displayItem = toSell.getDisplayItem();
        NBTCompound nbt = new NBTCompound(displayItem);

        nbt.add("ID", confirmInventory.getInventoryID());
        nbt.add("Slot", item.getItem());

        inventory.setItem(item.getSlot(), nbt.write(displayItem));

        return inventory;
    }

    /**
     * Get an inventory with inventory ID
     *
     * @param inventoryID The ID of the inventory
     * @return
     */
    public CInventoryData getInventory(String inventoryID) {
        for (CInventoryData inventory : this.inventories) {
            if (inventory.getInventoryID().equalsIgnoreCase(inventoryID)) {
                return inventory;
            }
        }

        return null;
    }

    public CInventoryData getInventory(Inventory i) {
        for (CInventoryData inventory : this.inventories) {
            if (inventory.equals(i)) {
                return inventory;
            }
        }

        return null;
    }

    public Inventory buildInventory(String inventoryID) {
        CInventoryData inventory = getInventory(inventoryID);
        if (inventory == null) {
            return null;
        }

        return inventory.buildInventory();
    }

}
