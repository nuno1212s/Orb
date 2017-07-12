package com.nuno1212s.mercado.searchmanager.searchparameters;

import com.nuno1212s.mercado.marketmanager.Item;
import com.nuno1212s.mercado.searchmanager.SearchParameter;
import com.nuno1212s.util.SerializableItem;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

/**
 * Currency searches
 */
public class CurrencySearchParameter extends SearchParameter {

    private boolean cash;

    public CurrencySearchParameter(String name, String param) {
        super(name);
        this.cash = Boolean.parseBoolean(param);
    }

    @Override
    public SearchParameters getParameterType() {
        return SearchParameters.CURRENCY;
    }

    @Override
    public ItemStack formatItem(JSONObject item) {
        return new SerializableItem(item);
    }

    @Override
    public boolean fitsSearch(Item item) {
        return item.isServerCurrency() != cash;
    }
}
