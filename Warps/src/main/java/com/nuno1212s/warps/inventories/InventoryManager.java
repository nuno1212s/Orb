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

    private File inventoryFile;

    public InventoryManager(Module m) {
        inventoryFile = new File(m.getDataFolder() + File.separator + "inventories" + File.separator);

        reloadInventories();
    }

    public void reloadInventories() {
        inventories = new ArrayList<>();

        if (!this.inventoryFile.exists()) {
            inventoryFile.mkdirs();
        }

        for (File file : this.inventoryFile.listFiles()) {
            try (FileReader r = new FileReader(file)) {
                inventories.add(new WInventoryData((JSONObject) new JSONParser().parse(r)));
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public Inventory getDefaultInventory() {
        return getInventory("mainInventory");
    }

    public Inventory getInventory(String inventoryID) {
        for (WInventoryData inventory : inventories) {
            if (inventory.getInventoryID().equalsIgnoreCase(inventoryID)) {
                return inventory.buildInventory();
            }
        }
        return null;
    }

}
