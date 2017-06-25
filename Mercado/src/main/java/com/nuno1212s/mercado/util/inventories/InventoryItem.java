package com.nuno1212s.mercado.util.inventories;

import com.nuno1212s.util.SerializableItem;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Inventory items
 */
@Getter
@ToString
public class InventoryItem {

    private ItemStack item;

    private List<String> itemFlags;

    private int slot;

    public InventoryItem(JSONObject data) {
        if (!data.containsKey("Item")) {
            this.item = null;
        } else {
            this.item = new SerializableItem((JSONObject) data.get("Item"));
        }
        this.slot = ((Long) data.get("Slot")).intValue();
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
