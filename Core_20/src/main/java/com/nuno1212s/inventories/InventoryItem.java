package com.nuno1212s.inventories;

import com.google.common.collect.ImmutableList;
import com.nuno1212s.util.SerializableItem;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class InventoryItem {

    protected ItemStack item;

    protected ImmutableList<String> itemFlags;

    protected int slot;

    protected String connectingInv = null;

    public InventoryItem(JSONObject data) {
        if (!data.containsKey("Item")) {
            this.item = null;
        } else {
            this.item = new SerializableItem((JSONObject) data.get("Item"));
        }
        this.slot = ((Long) data.get("Slot")).intValue();
        this.itemFlags = data.containsKey("Flags") ? ImmutableList.copyOf((List<String>) data.get("Flags")) : ImmutableList.<String>builder().build();

        for (String s : this.getItemFlags()) {
            if (s.startsWith("CONNECTING_INV")) {
                this.connectingInv = s.split(":")[1];
            }
        }
    }

    /**
     * Check if the items
     * @param itemFlag
     * @return
     */
    public boolean hasItemFlag(String itemFlag) {
        for (String flag : this.itemFlags) {
            if (flag.equalsIgnoreCase(itemFlag))
                return true;
        }
        return false;
    }

    /**
     * Get the inventory this inventory connects to
     * @return
     */
    public String getConnectingInventory() {
        return this.connectingInv;
    }

}
