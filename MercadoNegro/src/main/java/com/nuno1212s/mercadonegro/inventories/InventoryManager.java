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

    private File file;


    public InventoryManager(Module m) {
        inventories = new HashMap<>();
        this.previousPageItem = new Pair<>(0, new ItemStack(Material.AIR));
        this.nextPageItem = new Pair<>(1, new ItemStack(Material.AIR));
        file = m.getFile("inventories.json", false);

        JSONObject jsonObject;

        try (Reader r = new FileReader(file)) {

            jsonObject = (JSONObject) new JSONParser().parse(r);

        } catch (IOException | ParseException e) {
            System.out.println("Failed to load inventories");
            return;
        }

        Map<String, Object> inventories = (Map<String, Object>) jsonObject.get("Inventories");
        inventories.forEach((page, inventory) -> {
            int iPage = Integer.parseInt(page);
            CInventory inv = new CInventory((Map<String, Object>) inventory);
            this.inventories.put(iPage, inv);
        });

        Map<String, Object> previousPageItem = (Map<String, Object>) jsonObject.get("PreviousPageItem"),
                nextPageItem = (Map<String, Object>) jsonObject.get("NextPageItem");

        try {
            this.previousPageItem = new Pair<>(((Long) previousPageItem.get("Slot")).intValue(),
                    ItemUtils.itemFrom64((String) previousPageItem.get("Item")));
            this.nextPageItem = new Pair<>(((Long) nextPageItem.get("Slot")).intValue(),
                    ItemUtils.itemFrom64((String) nextPageItem.get("Item")));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void save() {
        JSONObject data = new JSONObject(), inventories = new JSONObject(), previousPage = new JSONObject(), nextPage = new JSONObject();

        this.inventories.forEach((page, inventory) ->
            inventories.put(String.valueOf(page), inventory.toJSONData())
        );

        data.put("Inventories", inventories);

        previousPage.put("Slot", this.previousPageItem.getKey());
        previousPage.put("Item", ItemUtils.itemTo64(this.previousPageItem.getValue()));

        data.put("PreviousPageItem", previousPage);

        nextPage.put("Slot", this.nextPageItem.getKey());
        nextPage.put("Item", ItemUtils.itemTo64(this.nextPageItem.getValue()));

        data.put("NextPageItem", nextPage);

        try (Writer w = new FileWriter(this.file)) {
            data.writeJSONString(w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CInventory getInventory(int page) {
        return this.inventories.get(page);
    }

    public Inventory getStartingInventory() {
        return buildInventory(1);
    }

    public Inventory buildInventory(int page) {
        CInventory inventory = getInventory(page);

        if (inventory == null) {
            return Bukkit.getServer().createInventory(null, 27, ChatColor.RED + "Black Market");
        }

        return inventory.getInventory((this.inventories.size() > page), page > 1);
    }

    public int addInventory(CInventory inventory) {
        int i = this.inventories.size() + 1;
        this.inventories.put(i, inventory);
        return i;
    }

    public void removeInventory(int page) {
        if (this.inventories.containsKey(page)) {
            //Check to see if it is the last page
            if (page == this.inventories.size()) {
                this.inventories.remove(page);
            } else {
                for (int i = page; i <= this.inventories.size(); i++) {
                    if (i == page) {
                        this.inventories.remove(page);
                    } else {
                        this.inventories.put(i - 1, this.inventories.get(i));
                        this.inventories.remove(i);
                    }
                }
            }
        }
    }

    public boolean isInventory(String inventoryName) {
        if (inventoryName.equalsIgnoreCase(ChatColor.RED + "Black Market")) {
            return true;
        }

        for (CInventory cInventory : this.inventories.values()) {
            if (cInventory.getInventoryName().equalsIgnoreCase(inventoryName)) {
                return true;
            }
        }

        return false;
    }

    public CInventory getInventory(String inventory) {
        for (CInventory cInventory : this.inventories.values()) {
            if (cInventory.getInventoryName().equalsIgnoreCase(inventory)) {
                return cInventory;
            }
        }
        return null;
    }

    public int getPage(CInventory c) {
        for (Map.Entry<Integer, CInventory> inventoryEntry : this.inventories.entrySet()) {
            if (inventoryEntry.getValue().equals(c)) {
                return inventoryEntry.getKey();
            }
        }
        return 0;
    }

    public void setPreviousPageItem(int distanceToLastSlot, ItemStack item) {
        this.previousPageItem = new Pair<>(distanceToLastSlot, item);
    }

    public void setNextPageItem(int distanceToLastSlot, ItemStack item) {
        this.nextPageItem = new Pair<>(distanceToLastSlot, item);
    }

    public boolean isPreviousPageSlot(Inventory inv, int slot) {
        return ((inv.getSize() - 1) - slot) == this.previousPageItem.getKey();
    }

    public boolean isNextPageSlot(Inventory inv, int slot) {
        return ((inv.getSize() - 1) - slot) == this.nextPageItem.getKey();
    }


}
