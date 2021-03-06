package com.nuno1212s.mercado.util.searchinventories;

import com.google.common.collect.ImmutableList;
import com.nuno1212s.mercado.searchmanager.SearchParameter;
import com.nuno1212s.inventories.InventoryItem;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Search inventory listeners item
 */
@ToString
public class SInventoryItem extends InventoryItem {

    @Getter
    SearchParameter searchParameter;

    public SInventoryItem(JSONObject object) {
        super(object);


    }

    public boolean hasConnectingInventory() {
        return this.connectingInv != null;
    }

    public SInventoryItem(ItemStack item, int slot, List<String> itemFlags, SearchParameter parameter) {
        super(item, ImmutableList.copyOf(itemFlags), slot, null, new ArrayList<>());
        this.searchParameter = parameter;

        for (String s : this.getItemFlags()) {
            if (s.contains("CONNECTING_INV")) {
                String[] split = s.split(":");
                this.connectingInv = split[1];
            }
        }
    }

}
