package com.nuno1212s.mercadonegro.inventories;

import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.util.Pair;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages inventories
 */
public class InventoryManager {

    @Getter
    private Map<Integer, CInventory> inventories;

    /**
     * Integer - The distance to the last slot of the inventory (slot = Inventory.size() - Pair.getKey())
     */
    @Getter
    private Pair<Integer, ItemStack> previousPageItem, nextPageItem;


    public InventoryManager(Module m) {
        inventories = new HashMap<>();
        File file = m.getFile("inventories.json", false);

        JSONObject jsonObject;

        try (Reader r = new FileReader(file)) {

            jsonObject = (JSONObject) new JSONParser().parse(r);

        } catch (IOException | ParseException e) {
            System.out.println("Failed to load inventories");
            return;
        }

    }

    public CInventory getInventory(int page) {
        return this.inventories.get(page);
    }

    public void setPreviousPageItem(int distanceToLastSlot, ItemStack item) {
        this.previousPageItem = new Pair<>(distanceToLastSlot, item);
    }

    public void setNextPageItem(int distanceToLastSlot, ItemStack item) {
        this.nextPageItem = new Pair<>(distanceToLastSlot, item);
    }


}
