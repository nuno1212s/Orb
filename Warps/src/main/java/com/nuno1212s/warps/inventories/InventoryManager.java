package com.nuno1212s.warps.inventories;

import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.warps.inventories.invdata.WInventoryData;
import org.bukkit.inventory.Inventory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages inventories
 */
public class InventoryManager {

    private List<WInventoryData> inventories;

    public InventoryManager(Module m) {
        inventories = new ArrayList<>();

        File inventory = new File(m.getDataFolder() + File.separator + "inventories" + File.separator);

        for (File inventoryFile : inventory.listFiles()) {
            try (FileReader r = new FileReader(inventoryFile)) {
                inventories.add(new WInventoryData((JSONObject) new JSONParser().parse(r)));
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }

    }

    public Inventory getInventory(String inventoryID) {
        for (WInventoryData inventory : inventories) {
            if (inventory.getInventoryID().equalsIgnoreCase(inventoryID)) {
                return inventory.buildInventory();
            }
        }
        return null;
    }

    public WInventoryData getInventory(Inventory i) {
        for (WInventoryData inventory : this.inventories) {
            if (inventory.equals(i)) {
                return inventory;
            }
        }
        return null;
    }

}
