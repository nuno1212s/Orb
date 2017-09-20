package com.nuno1212s.mercado.util.searchinventories;

import com.nuno1212s.mercado.searchmanager.SearchParameter;
import com.nuno1212s.util.inventories.InventoryItem;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.List;

/**
 * Search inventory listeners item
 */
@ToString
public class SInventoryItem extends InventoryItem {

    @Getter
    SearchParameter searchParameter;

    @Getter
    private String connectingInventory;

    public SInventoryItem(JSONObject object) {
        super(object);

        for (String s : this.getItemFlags()) {
            if (s.contains("CONNECTING_INV")) {
                String[] split = s.split(":");
                connectingInventory = split[1];
            }
        }

    }

    public boolean hasConnectingInventory() {
        return connectingInventory != null;
    }

    public SInventoryItem(ItemStack item, int slot, List<String> itemFlags, SearchParameter parameter) {
        super(item, itemFlags, slot);
        this.searchParameter = parameter;

        for (String s : this.getItemFlags()) {
            if (s.contains("CONNECTING_INV")) {
                String[] split = s.split(":");
                connectingInventory = split[1];
            }
        }
    }

}
