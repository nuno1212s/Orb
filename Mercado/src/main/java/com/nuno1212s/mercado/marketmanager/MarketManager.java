package com.nuno1212s.mercado.marketmanager;

import com.nuno1212s.mercado.main.Main;
import com.nuno1212s.mercado.util.chathandlers.ChatHandlerManager;
import com.nuno1212s.mercado.util.inventories.InventoryData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.util.Pair;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles markets
 */
public class MarketManager {

    @Getter
    private List<Item> marketItems;

    @Getter
    private InventoryData landingInventoryData, mainInventoryData, confirmInventoryData, sellInventory;

    @Getter
    public ChatHandlerManager chatManager;

    private Map<UUID, Integer> pages;

    public MarketManager(Module module) {
        marketItems = new ArrayList<>();
        pages = new HashMap<>();
        chatManager = new ChatHandlerManager();

        File file = new File(module.getDataFolder() + File.separator + "inventories");
        if (!file.exists()) {
            file.mkdirs();
        }

        this.landingInventoryData = new InventoryData(
                module.getFile("inventories" + File.separator + "landinginventory.json", true));
        this.mainInventoryData = new InventoryData(
                module.getFile("inventories" + File.separator + "maininventory.json", true));
        this.confirmInventoryData = new InventoryData(
                module.getFile("inventories" + File.separator + "confirminventory.json", true));
        this.sellInventory = new InventoryData(
                module.getFile("inventories" + File.separator + "sellInventory.json", true));
    }

    /**
     * Add an item to the item list
     *
     * @param item item to add
     */
    public void addItem(Item item) {
        marketItems.add(item);
        Main.getIns().getMySql().addItem(item);
    }

    /**
     * Remove an item from the items
     *
     * @param itemID The ID of the Item
     */
    public void removeItem(String itemID) {
        marketItems.remove(getItem(itemID));
        Main.getIns().getMySql().removeItem(itemID);
    }

    /**
     * Get an item with a specific ID
     *
     * @param itemID The item ID
     */
    public Item getItem(String itemID) {
        for (Item marketItem : marketItems) {
            if (marketItem.getItemID().equals(itemID)) {
                return marketItem;
            }
        }
        return null;
    }

    /**
     * Get all the items for a certain player
     *
     * @param player The player ID
     */
    public List<Item> getPlayerItems(UUID player) {
        //Use a normal stream because parallel streams have much higher overhead and are not efficient for a small list
        return this.marketItems.stream()
                .filter(item -> item.getOwner().equals(player))
                .collect(Collectors.toList());
    }

    /**
     * Get all the current active items for a certain player
     *
     *
     */
    public List<Item> getActivePlayerItems(UUID player) {
        return this.marketItems.stream()
                .filter(item -> item.getOwner().equals(player) && !item.isSold())
                .collect(Collectors.toList());
    }

    /**
     * Get the items on the market
     *
     * @param page The page of the market
     * @param itemsPerPage The amount of items that should be shown per page
     */
    private List<Item> getItemsForPage(int page, int itemsPerPage) {
        if (this.marketItems.size() < itemsPerPage && page > 1) {
            return new ArrayList<>();
        }

        int startingItem = (page - 1) * itemsPerPage, endItem = startingItem + itemsPerPage;
        if (endItem > this.marketItems.size()) {
            return this.marketItems.subList(startingItem, this.marketItems.size());
        } else {
            return this.marketItems.subList(startingItem, endItem);
        }
    }

    /**
     * Get the landing inventory for the market (Not the actual market inventory, {@link #openInventory(Player, int)})
     * @return
     */
    public Inventory getLandingInventory() {
        return this.landingInventoryData.buildInventory();
    }

    /**
     * Open the inventory page at a specific page
     *
     * @param p The player to open it to
     * @param page The page to open
     */
    public void openInventory(Player p, int page) {
        p.openInventory(getInventory(page));
        this.pages.put(p.getUniqueId(), page);
    }

    /**
     * Get the inventory with all the items
     * @param page the page of the inventory
     */
    private Inventory getInventory(int page) {
        Inventory inventory = this.mainInventoryData.buildInventory();

        List<Item> itemsForPage = getItemsForPage(page, this.mainInventoryData.getInventorySize() - 18);

        int currentSlot = 0;
        for (Item item : itemsForPage) {
            inventory.setItem(currentSlot++, item.getDisplayItem());
        }

        return inventory;
    }

    /**
     * Get the page a player is currently at
     * @param player The player
     */
    public int getPage(UUID player) {
        return this.pages.get(player);
    }

    /**
     * Remove the page a player is watching (When the inventory is closed)
     * @param player
     */
    public void removePage(UUID player) {
        this.pages.remove(player);
    }

}
