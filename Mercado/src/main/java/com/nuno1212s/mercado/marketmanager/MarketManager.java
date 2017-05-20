package com.nuno1212s.mercado.marketmanager;

import com.nuno1212s.mercado.main.Main;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles markets
 */
public class MarketManager {

    @Getter
    private List<Item> marketItems;

    public MarketManager() {
        marketItems = new ArrayList<>();
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

}
