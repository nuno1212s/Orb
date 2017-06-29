package com.nuno1212s.mercado.util.searchinventories;

import com.nuno1212s.mercado.searchmanager.SearchParameter;
import com.nuno1212s.mercado.util.inventories.InventoryItem;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.List;

/**
 * Search inventory item
 */
public class SInventoryItem extends InventoryItem{

    @Getter
    SearchParameter searchParameter;

    public SInventoryItem(JSONObject object) {
        super(object);

        //TODO: set the search parameter
    }

    public SInventoryItem(ItemStack item, int slot, List<String> itemFlags, SearchParameter parameter) {
        super(item, itemFlags, slot);
        this.searchParameter = parameter;
    }

}
