package com.nuno1212s.mercado.searchmanager;

import com.nuno1212s.mercado.marketmanager.Item;
import com.nuno1212s.mercado.searchmanager.searchparameters.SearchParameters;
import lombok.ToString;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

/**
 * Search parameter
 */
@ToString
public abstract class SearchParameter {

    private String name;

    public SearchParameter(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public abstract boolean fitsSearch(Item item);

    public abstract ItemStack formatItem(JSONObject item);

    public abstract SearchParameters getParameterType();

}
