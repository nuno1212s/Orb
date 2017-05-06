package com.nuno1212s.classes.classmanager;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.json.simple.JSONObject;

import java.util.Map;

/**
 * Display Inventory
 */
public class DisplayInventory {

    @Getter
    private String inventoryName;

    private Kit[] items;

    public DisplayInventory(JSONObject displayData, KitManager manager) {
        int inventorySize = ((Long) displayData.get("InventorySize")).intValue();
        this.inventoryName = ChatColor.translateAlternateColorCodes('&', (String) displayData.get("InventoryName"));
        this.items = new Kit[inventorySize];
        Map<String, Object> items1 = (Map<String, Object>) displayData.get("Items");

        items1.forEach((slot, kitID) -> {
            int iSlot = Integer.parseInt(slot);
            int iKitID = ((Long) kitID).intValue();
            items[iSlot] = manager.getKit(iKitID);
        });
    }

    public Kit getKitAtSlot(int slot) {
        return items[slot];
    }

    public Inventory getInventory(Player player) {
        Inventory i = Bukkit.getServer().createInventory(null, items.length, inventoryName);
        for (int i2 = 0; i2 < items.length; i2++) {
            Kit item = items[i2];
            if (item == null) {
                continue;
            }
            i.setItem(i2, item.getDisplayItem(player).clone());
        }
        return i;
    }

}
