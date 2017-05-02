package com.nuno1212s.classes.classmanager;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class items
 */
public class Class {

    @Getter
    private String className, permission;

    @Getter
    private ItemStack[] items;

    public Class(String className, String permissionNode, ItemStack[] items) {
        this.className = className;
        this.permission = permissionNode;
        this.items = items;
    }

    public Class(Map<String, Object> data) {
        this.className = (String) data.get("ClassName");
        this.permission = (String) data.get("Permission");
        this.items = new ItemStack[(Integer) data.get("InventorySize")];
        Map<String, Object> items = (Map<String, Object>) data.get("Items");
        items.forEach((slot, item) -> {
            int slo = Integer.parseInt(slot);
            ItemStack itemStack;
            try {
                itemStack = ClassManager.itemFrom64((String) item);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            this.items[slo] = itemStack;
        });
    }

    public void setItem(int slot, ItemStack item) {
        this.items[slot] = item;
    }

    public Inventory getClassItems() {
        Inventory i = Bukkit.getServer().createInventory(null, items.length, className);

        i.setContents(items);

        return i;
    }

    public void updateItems(Inventory updatedItems) {
        this.items = updatedItems.getContents();
    }

    public void addItems(Player p) {
        for (ItemStack item : items) {
            p.getInventory().addItem(item.clone());
        }
    }

    public Map<String, Object> save() {
        Map<String, Object> saved = new HashMap<>(), items = new HashMap<>();

        saved.put("ClassName", className);
        saved.put("Permission", permission);
        saved.put("InventorySize", this.items.length);

        for (int i = 0; i < this.items.length; i++) {
            if (this.items[i] == null) {
                continue;
            }

            items.put(String.valueOf(i), ClassManager.itemTo64(this.items[i]));

        }

        saved.put("Items", items);

        return saved;
    }

}
