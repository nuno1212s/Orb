package com.nuno1212s.mercadonegro.inventories;

import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.util.ItemUtils;
import com.nuno1212s.util.Pair;
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

    public InventoryManager(Module m) {
        this.inventories = new ArrayList<>();

        File dataFolder = new File(m.getDataFolder() + File.separator + "Inventories" + File.separator);

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
            saveDefaultInventory(m);
        }

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
