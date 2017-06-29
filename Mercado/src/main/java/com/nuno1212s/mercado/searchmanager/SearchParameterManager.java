package com.nuno1212s.mercado.searchmanager;

import com.nuno1212s.mercado.marketmanager.Item;
import com.nuno1212s.mercado.util.searchinventories.SInventoryData;
import com.nuno1212s.modulemanager.Module;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * Search parameter
 */
public class SearchParameterManager {

    private Map<UUID, SearchParameterBuilder> searchParametersPlayer;

    private List<SInventoryData> inventories;

    private SInventoryData landingInventory;

    private Module m;

    public SearchParameterManager(Module m) {
        this.m = m;
        searchParametersPlayer = new HashMap<>();
        inventories = new ArrayList<>();
        loadParameters(m.getFile("searchparameters.json", true));
    }

    public void loadParameters(File f) {
        JSONObject json;

        try (Reader r = new FileReader(f)) {
            json = (JSONObject) new JSONParser().parse(r);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
            return;
        }

        List<String> inventories = (List<String>) json.get("Inventories");
        for (String inventory : inventories) {
            this.inventories.add(loadInventory(inventory));
        }

        String landingInventory = (String) json.get("LandingInventory");
        this.landingInventory = loadInventory(landingInventory);
    }

    SInventoryData loadInventory(String inventoryName) {
        File f = m.getFile("searchinventories" + File.separator + inventoryName + ".json", true);

        JSONObject json;

        try (Reader r = new FileReader(f)) {
            json = (JSONObject) new JSONParser().parse(r);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
            return null;
        }

        return new SInventoryData(json);

    }

    public SInventoryData getInventory(String inventoryID) {
        for (SInventoryData datum : this.inventories) {
            if (datum.getInventoryID().equalsIgnoreCase(inventoryID)) {
                return datum;
            }
        }
        return null;
    }

    public Inventory getSearchParameterInventory(UUID player) {

        Inventory inventory;

        if (this.searchParametersPlayer.containsKey(player)) {
            inventory = this.landingInventory.buildInventory(this.searchParametersPlayer.get(player));
        } else {
            SearchParameterBuilder newBuilder = SearchParameterBuilder.builder();
            this.searchParametersPlayer.put(player, newBuilder);
            inventory = this.landingInventory.buildInventory(newBuilder);
        }

        return inventory;
    }

    public boolean hasSearchParameters(UUID player) {
        return this.searchParametersPlayer.containsKey(player);
    }

    public void removeSearchParameters(UUID player) {
        this.searchParametersPlayer.remove(player);
    }

    public SearchParameter[] getSearchParameters(UUID player) {
        if (searchParametersPlayer.containsKey(player)) {
            return searchParametersPlayer.get(player).build();
        }

        SearchParameterBuilder builder = SearchParameterBuilder.builder();
        this.searchParametersPlayer.put(player, builder);
        return builder.build();
    }

    public boolean fitsSearch(Item item, SearchParameter[] parameters) {
        for (SearchParameter searchParameter : parameters) {
            if (!searchParameter.fitsSearch(item)) {
                return false;
            }
        }
        return true;
    }

    public ItemStack buildItem(ItemStack item, UUID player) {
        SearchParameter[] parameters;
        if (searchParametersPlayer.containsKey(player)) {
            parameters = searchParametersPlayer.get(player).build();
        } else {
            parameters = new SearchParameter[]{};
        }
        return buildItem(item, parameters);
    }

    public ItemStack buildItem(ItemStack item, SearchParameter[] parameters) {
        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore = itemMeta.getLore();
        for (SearchParameter parameter : parameters) {
            lore.add(parameter.getName());
        }
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }

}
