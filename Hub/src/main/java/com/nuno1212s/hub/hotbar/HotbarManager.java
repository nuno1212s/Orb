package com.nuno1212s.hub.hotbar;

import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.util.inventories.InventoryItem;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages hotbar
 */
public class HotbarManager {

    private List<InventoryItem> items;

    public HotbarManager(Module m) {
        this.items = new ArrayList<>();

        File dataFile = m.getFile("hotbar.json", true);

        JSONArray items;

        try (FileReader r = new FileReader(dataFile)) {

            items = (JSONArray) new JSONParser().parse(r);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }

        items.forEach(item ->
            this.items.add(new InventoryItem((JSONObject) item))
        );

    }

    /**
     * Get the items for the hot bar
     *
     * @return
     */
    public Map<Integer, ItemStack> getItems() {
        Map<Integer, ItemStack> items = new HashMap<>();

        this.items.forEach((item) ->
            items.put(item.getSlot(), item.getItem().clone())
        );

        return items;
    }

    /**
     * Get the item for a specific slot
     *
     * @param slot
     * @return
     */
    public InventoryItem getItem(int slot) {
        for (InventoryItem item : this.items) {
            if (item.getSlot() == slot) {
                return item;
            }
        }

        return null;
    }



}
