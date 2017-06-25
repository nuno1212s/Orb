package com.nuno1212s.mercado.marketmanager;

import com.nuno1212s.main.MainData;
import com.nuno1212s.mercado.main.Main;
import com.nuno1212s.mercado.util.chathandlers.ChatHandlerManager;
import com.nuno1212s.mercado.util.inventories.InventoryData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.ItemUtils;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import com.nuno1212s.util.SerializableItem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles markets
 */
public class MarketManager {

    @Getter
    private List<Item> marketItems;

    @Getter
    private InventoryData landingInventoryData, mainInventoryData, confirmInventoryData, sellInventory, ownInventory;

    @Getter
    public ChatHandlerManager chatManager;

    @Getter
    private ItemStack cashItem, coinsItems;

    private Map<UUID, Integer> pages;

    public MarketManager(Module module) {
        marketItems = new ArrayList<>();
        marketItems = Main.getIns().getMySql().getAllItems();
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
        this.ownInventory = new InventoryData(
                module.getFile("inventories" + File.separator + "owninventory.json", true));
        this.sellInventory = new InventoryData(
                module.getFile("inventories" + File.separator + "sellInventory.json", true));

        JSONObject json;

        try (Reader r = new FileReader(module.getFile("config.json", true))) {
            json = (JSONObject) new JSONParser().parse(r);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
            return;
        }

        this.cashItem = new SerializableItem((JSONObject) json.get("CashItem"));
        this.coinsItems = new SerializableItem((JSONObject) json.get("CoinsItem"));

    }

    /**
     * Add an item to the item list
     *
     * @param item item to add
     */
    public void addItem(Item item) {
        marketItems.add(item);

        MainData.getIns().getScheduler().runTaskAsync(() -> {
            Main.getIns().getMySql().addItem(item);
        });
    }

    /**
     * Remove an item from the items
     *
     * @param itemID The ID of the Item
     */
    public void removeItem(String itemID) {
        marketItems.remove(getItem(itemID));

        MainData.getIns().getScheduler().runTaskAsync(() -> {
            Main.getIns().getMySql().removeItem(itemID);
        });
    }

    /**
     *
     */
    public void sellItem(Item item) {
        MainData.getIns().getScheduler().runTaskAsync(() -> {
            Main.getIns().getMySql().updateItem(item);
        });
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
     */
    public List<Item> getActivePlayerItems(UUID player) {
        return this.marketItems.stream()
                .filter(item -> item.getOwner().equals(player) && !item.isSold())
                .collect(Collectors.toList());
    }

    /**
     * Get the items on the market
     *
     * @param page         The page of the market
     * @param itemsPerPage The amount of items that should be shown per page
     */
    private List<Item> getItemsForPage(int page, int itemsPerPage) {
        List<Item> marketItems = new ArrayList<>(this.marketItems);
        marketItems.removeIf(Item::isSold);
        if (marketItems.size() < itemsPerPage && page > 1) {
            return new ArrayList<>();
        }

        int startingItem = (page - 1) * itemsPerPage, endItem = startingItem + itemsPerPage;
        if (endItem > marketItems.size()) {
            return marketItems.subList(startingItem, marketItems.size());
        } else {
            return marketItems.subList(startingItem, endItem);
        }
    }

    /**
     *
     */
    private List<Item> getOwnItemsForPage(UUID player, int page, int itemsPerPage) {
        List<Item> marketItems = getPlayerItems(player);

        if (marketItems.size() < itemsPerPage && page > 1) {
            return new ArrayList<>();
        }

        int startingItem = (page - 1) * itemsPerPage, endItem = startingItem + itemsPerPage;
        System.out.println(startingItem);
        System.out.println(endItem);
        if (endItem > marketItems.size()) {
            return marketItems.subList(startingItem, marketItems.size());
        } else {
            return marketItems.subList(startingItem, endItem);
        }
    }

    /**
     * Get the landing inventory for the market (Not the actual market inventory, {@link #openInventory(Player, int)})
     *
     * @return The landing inventory
     */
    public Inventory getLandingInventory() {
        return this.landingInventoryData.buildInventory();
    }

    /**
     * Open the inventory page at a specific page
     *
     * @param p    The player to open it to
     * @param page The page to open
     */
    public void openInventory(Player p, int page) {
        Inventory inventory = getInventory(page);
        this.pages.put(p.getUniqueId(), page);
        p.openInventory(inventory);
    }

    /**
     * Get the inventory with all the items
     *
     * @param page the page of the inventory
     */
    private Inventory getInventory(int page) {
        Inventory inventory = this.mainInventoryData.buildInventory();

        List<Item> itemsForPage = getItemsForPage(page, this.mainInventoryData.getInventorySize() - 18);

        int currentSlot = 0;
        for (Item item : itemsForPage) {
            inventory.setItem(currentSlot++, item.getDisplayItem().clone());
        }

        return inventory;
    }

    /**
     * Get the inventory that displays the player has sold / has on sale
     *
     * @param player
     * @param page
     * @return
     */
    public Inventory getOwnItemInventory(UUID player, int page) {
        this.pages.put(player, page);
        System.out.println(this.ownInventory);
        List<Item> ownItemsForPage = getOwnItemsForPage(player, page, this.ownInventory.getInventorySize() - 9);

        ownItemsForPage.sort(new Comparator<Item>() {
                              @Override
                              public int compare(Item o1, Item o2) {
                                  int compare = Boolean.compare(o1.isSold(), o2.isSold());
                                  if (compare == 0) {
                                      if (o1.isSold()) {
                                          compare = Long.compare(o1.getSoldTime(), o2.getSoldTime());
                                      } else {
                                          compare = Long.compare(o1.getPlaceTime(), o2.getPlaceTime());
                                      }
                                  }
                                  return compare;
                              }
                          }
        );

        Inventory inventory = this.ownInventory.buildInventory();

        int currentSlot = 0;

        for (Item item : ownItemsForPage) {
            inventory.setItem(currentSlot++, item.getDisplayItemOwn());
        }

        return inventory;
    }

    /**
     * Get the page a player is currently at
     *
     * @param player The player
     */
    public int getPage(UUID player) {
        return this.pages.get(player);
    }

    /**
     * Remove the page a player is watching (When the inventory is closed)
     *
     * @param player
     */
    public void removePage(UUID player) {
        this.pages.remove(player);
    }

}
