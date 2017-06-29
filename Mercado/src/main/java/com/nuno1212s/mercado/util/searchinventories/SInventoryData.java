package com.nuno1212s.mercado.util.searchinventories;

import com.nuno1212s.mercado.searchmanager.SearchParameter;
import com.nuno1212s.mercado.searchmanager.SearchParameterBuilder;
import com.nuno1212s.mercado.searchmanager.searchparameters.SearchParameters;
import com.nuno1212s.mercado.util.inventories.InventoryData;
import com.nuno1212s.mercado.util.inventories.InventoryItem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Search inventory data
 */
public class SInventoryData extends InventoryData {

    @Getter
    private String inventoryID;

    public SInventoryData(JSONObject e) {
        super(e);

        //Clear the previous items, because we want to load our own items
        this.items = new ArrayList<>();
        this.inventoryID = (String) e.get("InventoryID");

        JSONArray items = (JSONArray) e.get("InventoryItems");

        for (JSONObject item : (List<JSONObject>) items) {
            this.items.addAll(loadItemData(item));
        }

    }

    public Inventory buildInventory(SearchParameterBuilder parameters) {
        Inventory inv = Bukkit.getServer().createInventory(null, inventorySize, inventoryName);

        for (InventoryItem item : items) {
            if (item instanceof SInventoryItem) {
                if (((SInventoryItem) item).getSearchParameter() != null) {

                    if (parameters.containsParameter(((SInventoryItem) item).getSearchParameter())) {
                        ItemStack item1 = item.getItem();
                        item1.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                        ItemMeta itemMeta = item1.getItemMeta();
                        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        item1.setItemMeta(itemMeta);
                        inv.setItem(item.getSlot(), item1);
                        break;
                    }

                }
            }
            inv.setItem(item.getSlot(), item.getItem());
        }

        return inv;
    }

    private List<InventoryItem> loadItemData(JSONObject itemData) {
        ArrayList<InventoryItem> items = new ArrayList<>();
        int startingSlot, endingSlot;

        Object slot = itemData.get("Slot");

        if (slot instanceof Long) {
            startingSlot = ((Long) slot).intValue();
            endingSlot = ((Long) slot).intValue();
        } else if (slot instanceof String) {
            String[] split = ((String) slot).split("-");
            startingSlot = Integer.parseInt(split[0]);
            endingSlot = Integer.parseInt(split[1]);
        } else {
            throw new IllegalArgumentException("Failed to load " + itemData);
        }

        int itemAmounts = (endingSlot - startingSlot) + 1;

        Object searchParameter = itemData.get("SearchParameter");

        JSONObject itemJSON = (JSONObject) itemData.get("Item");

        List<String> itemFlags = (List<String>) itemData.get("ItemFlags");

        if (searchParameter instanceof JSONArray) {
            int currentSlot = startingSlot;
            if (((JSONArray) searchParameter).size() != itemAmounts) {
                throw new IllegalArgumentException("The slot count does not match the item count");
            }
            for (Object obj : (JSONArray) searchParameter) {
                JSONObject search = (JSONObject) obj;
                String type = (String) search.get("Type");
                String name = (String) search.get("Name");
                String param = (String) search.get("Parameter");

                SearchParameter instantiate = SearchParameters.valueOf(type.toUpperCase()).instantiate(name, param);

                SInventoryItem item = new SInventoryItem(instantiate.formatItem(itemJSON), currentSlot++, itemFlags, instantiate);
                items.add(item);
            }

        } else if (searchParameter instanceof JSONObject) {

            JSONObject search = (JSONObject) searchParameter;
            String type = (String) search.get("Type");
            String name = (String) search.get("Name");
            String param = (String) search.get("Parameter");

            SearchParameter instantiate = SearchParameters.valueOf(type.toUpperCase()).instantiate(name, param);
            SInventoryItem item = new SInventoryItem(instantiate.formatItem(itemJSON), startingSlot, itemFlags, instantiate);

            items.add(item);
        } else if (searchParameter == null) {
            items.add(new InventoryItem(itemData));
        }

        return items;
    }

}
