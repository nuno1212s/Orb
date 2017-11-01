package com.nuno1212s.mercado.searchmanager;

import com.nuno1212s.mercado.marketmanager.Item;
import com.nuno1212s.mercado.searchmanager.searchparameters.SearchParameters;
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

        File file = new File(m.getDataFolder() + File.separator + "searchinventories" + File.separator);
        if (!file.exists()) {
            file.mkdirs();
        }

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
            SInventoryData e = loadInventory(inventory);
            if (e == null) {
                continue;
            }
            this.inventories.add(e);
        }

        String landingInventory = (String) json.get("LandingInventory");
        this.landingInventory = loadInventory(landingInventory);
    }

    SInventoryData loadInventory(String inventoryName) {
        String replace = inventoryName.replace("/", File.separator);
        String[] split = replace.split(File.separator);

        if (split.length > 1) {
            File f2 = new File(m.getDataFolder() + File.separator + "searchinventories" + File.separator + split[0]);
            if (!f2.exists()) {
                f2.mkdirs();
            }
        }

        File f = m.getFile("searchinventories" + File.separator + replace + ".json", true);

        JSONObject json;

        try (Reader r = new FileReader(f)) {
            json = (JSONObject) new JSONParser().parse(r);
        } catch (ParseException | IOException e) {
            System.out.println("Failed while loading the inventory: " + inventoryName);
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

        if (landingInventory.getInventoryID().equalsIgnoreCase(inventoryID)) {
            return landingInventory;
        }

        return null;
    }

    public SInventoryData getInventoryByName(String inventoryName) {
        for (SInventoryData datum : this.inventories) {
            if (datum.getInventoryName().equalsIgnoreCase(inventoryName)) {
                return datum;
            }
        }

        if (landingInventory.getInventoryName().equalsIgnoreCase(inventoryName)) {
            return landingInventory;
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

    public Inventory getSearchParameterInventory(UUID player, SInventoryData inventoryData) {

        Inventory inventory;

        if (this.searchParametersPlayer.containsKey(player)) {
            inventory = inventoryData.buildInventory(this.searchParametersPlayer.get(player));
        } else {
            SearchParameterBuilder newBuilder = SearchParameterBuilder.builder();
            this.searchParametersPlayer.put(player, newBuilder);
            inventory = inventoryData.buildInventory(newBuilder);
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

    public SearchParameterBuilder getSearchParameterBuilder(UUID player) {
        if (searchParametersPlayer.containsKey(player)) {
            return searchParametersPlayer.get(player);
        }

        SearchParameterBuilder builder = SearchParameterBuilder.builder();
        this.searchParametersPlayer.put(player, builder);
        return builder;
    }

    public boolean fitsSearch(Item item, SearchParameter[] parameters) {

        //Fits all mode of search
        /*if (player.isFitsAll()) {
            for (SearchParameter parameter : parameters) {
                if (!parameter.fitsSearch(item)) {
                    return false;
                }
            }
        }*/

        //Fits any mode of search
        if (parameters.length != 0) {
            for (SearchParameters parameter : SearchParameters.values()) {
                if (!fitsSearchType(parameter, parameters, item)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean fitsSearchType(SearchParameters type, SearchParameter[] parameters, Item item) {
        int amountOfParameters = 0;
        for (SearchParameter parameter : parameters) {
            if (type == parameter.getParameterType()) {
                amountOfParameters++;
                if (parameter.fitsSearch(item)) {
                    return true;
                }
            }
        }
        return amountOfParameters == 0;
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
        lore.add("");
        for (SearchParameter parameter : parameters) {
            lore.add(parameter.getName());
        }
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }

}
