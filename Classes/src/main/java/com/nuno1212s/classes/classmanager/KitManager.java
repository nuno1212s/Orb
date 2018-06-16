package com.nuno1212s.classes.classmanager;

import com.nuno1212s.classes.inventories.KInventoryData;
import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

/**
 * Manages classes
 */
public class KitManager {

    private List<Kit> kits;

    @Getter
    private File dataFile;


    public KitManager(Module m) {
        kits = new ArrayList<>();
        dataFile = m.getFile("classes.json", false);

        File dataFolder = new File(m.getDataFolder() + File.separator + "inventories" + File.separator);

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        for (File file : dataFolder.listFiles()) {
            new KInventoryData(file);
        }

        load();
    }

    public void load() {
        JSONObject dataFile;

        try (Reader reader = new FileReader(this.dataFile)) {
            JSONParser jsonParser = new JSONParser();
            dataFile = (JSONObject) jsonParser.parse(reader);
        } catch (IOException | ParseException e) {
            System.out.println("Could not read JSON file, maybe it's undefined?");
            return;
        }

        for (String o : (Set<String>) dataFile.keySet()) {
            this.kits.add(new Kit((Map<String, Object>) dataFile.get(o)));
        }

    }

    public void save() {
        JSONObject jsonObject = new JSONObject();

        this.kits.forEach(kit ->
                jsonObject.put(kit.getClassName(), kit.save())
        );

        try (Writer w = new FileWriter(this.dataFile)) {
            jsonObject.writeJSONString(w);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Get the class for the name provided
     *
     * @param kitName The name of the kit
     * @return
     */
    public Kit getKit(String kitName) {
        for (Kit aKit : this.kits) {
            if (kitName.equalsIgnoreCase(aKit.getClassName())) {
                return aKit;
            }
        }
        return null;
    }

    /**
     * Get the class for the id provided
     *
     * @param kitID The ID of the kit
     * @return
     */
    public Kit getKit(int kitID) {
        for (Kit aKit : this.kits) {
            if (aKit.getId() == kitID) {
                return aKit;
            }
        }
        return null;
    }

    /**
     * Get the class for the editing inventorylisteners
     *
     * @param name The inventorylisteners name
     * @return the kit that matches it
     */
    public Kit getKitEdit(String name) {
        for (Kit aKit : this.kits) {
            if (name.startsWith(aKit.getClassName() + " Edit")) {
                return aKit;
            }
        }
        return null;
    }

    /**
     * Is the inventorylisteners a display of a kit
     *
     * @param inventoryName The name of the inventorylisteners to check
     * @return
     */
    public boolean isKitDisplay(String inventoryName) {
        for (Kit aKit : this.kits) {
            if (inventoryName.equalsIgnoreCase(aKit.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void addKit(Kit k) {
        this.kits.add(k);
    }

    /**
     * Removes the kit from the kit list
     *
     * @param k The kit to remove
     */
    public void removeKit(Kit k) {
        this.kits.remove(k);
    }

    /**
     * Builds the inventorylisteners
     *
     * @return
     */
    public Inventory buildInventory(Player player) {
        InventoryData mainInventory = MainData.getIns().getInventoryManager().getInventory("KitMainInventory");
        if (mainInventory == null) {
            throw new IllegalArgumentException("There is no mainInventory for the /kits command");
        }
        return mainInventory.buildInventory(player);
    }

}

