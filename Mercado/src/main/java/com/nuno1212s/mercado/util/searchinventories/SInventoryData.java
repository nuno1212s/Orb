package com.nuno1212s.mercado.util.searchinventories;

import com.nuno1212s.main.MainData;
import com.nuno1212s.mercado.main.Main;
import com.nuno1212s.mercado.searchmanager.SearchParameter;
import com.nuno1212s.mercado.searchmanager.SearchParameterBuilder;
import com.nuno1212s.mercado.searchmanager.SearchParameterManager;
import com.nuno1212s.mercado.searchmanager.searchparameters.SearchParameters;
import com.nuno1212s.util.SerializableItem;
import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventories.InventoryItem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Search inventorylisteners data
 */
public class SInventoryData extends InventoryData<SInventoryItem> {

    public SInventoryData(JSONObject f) {
        super(f, SInventoryItem.class);

        this.directRedirect = false;

        //Clear the previous items, because we want to load our own items
        this.items = new ArrayList<>();

        JSONArray items = (JSONArray) f.get("InventoryItems");

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
                        ItemStack item1 = item.getItem().clone();
                        item1.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                        ItemMeta itemMeta = item1.getItemMeta();
                        List<String> lore = itemMeta.getLore() == null ? new ArrayList<>() : itemMeta.getLore();
                        lore.add("");
                        String search_selected = MainData.getIns().getMessageManager().getMessage("SEARCH_SELECTED").toString();
                        lore.add(search_selected);
                        itemMeta.setLore(lore);
                        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        item1.setItemMeta(itemMeta);
                        inv.setItem(item.getSlot(), item1);
                        continue;
                    } else {
                        ItemStack item1 = item.getItem().clone();
                        ItemMeta itemMeta = item1.getItemMeta();
                        List<String> lore = itemMeta.getLore() == null ? new ArrayList<>() : itemMeta.getLore();
                        lore.add("");
                        String search_select = MainData.getIns().getMessageManager().getMessage("SEARCH_SELECT").toString();
                        lore.add(search_select);
                        itemMeta.setLore(lore);
                        item1.setItemMeta(itemMeta);
                        inv.setItem(item.getSlot(), item1);
                        continue;
                    }

                }
            }

            inv.setItem(item.getSlot(), item.getItem());
        }

        return inv;
    }

    private List<SInventoryItem> loadItemData(JSONObject itemData) {
        ArrayList<SInventoryItem> items = new ArrayList<>();
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

        List<String> itemFlags = (List<String>) itemData.get("Flags");
        if (itemFlags == null) {
            itemFlags = new ArrayList<>();
        }

        if (searchParameter instanceof JSONArray) {
            int currentSlot = startingSlot;

            /*if (((JSONArray) searchParameter).size() != itemAmounts) {
                throw new IllegalArgumentException("The slot count does not match the item count");
            }*/

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

            SearchParameter instantiate;
            try {
                 instantiate = SearchParameters.valueOf(type.toUpperCase()).instantiate(name, param);
            } catch (Exception e) {
                System.out.println(itemData);
                System.out.println(type);

                return new ArrayList<>();
            }

            SInventoryItem item = new SInventoryItem(instantiate.formatItem(itemJSON), startingSlot, itemFlags, instantiate);

            items.add(item);
        } else if (searchParameter == null) {
            items.add(new SInventoryItem(new SerializableItem(itemJSON), startingSlot, itemFlags, null));
        }

        return items;
    }

}
