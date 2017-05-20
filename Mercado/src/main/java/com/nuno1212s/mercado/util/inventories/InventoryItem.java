package com.nuno1212s.mercado.util.inventories;

import com.nuno1212s.util.SerializableItem;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Inventory items
 */
@Getter
public class InventoryItem {

    private ItemStack item;

    private List<String> itemFlags;

    public InventoryItem(JSONObject data) {
        this.item = new SerializableItem(data);
        this.itemFlags = data.containsKey("Flags") ? (List<String>) data.get("Flags") : new ArrayList<>();
    }

    public boolean hasItemFlag(String itemFlag) {
        for (String flag : this.itemFlags) {
            if (flag.equalsIgnoreCase(itemFlag))
                return true;
        }
        return false;
    }

}
