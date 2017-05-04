package com.nuno1212s.classes.classmanager;

import lombok.Getter;
import lombok.Setter;
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
public class Kit {

    @Getter
    private int id;

    @Getter
    private String className, permission;

    @Getter
    private ItemStack[] items;

    @Getter
    @Setter
    private ItemStack displayItem;

    public Kit(int id, String className, String permissionNode, ItemStack[] items, ItemStack displayItem) {
        this.id = id;
        this.className = className;
        this.permission = permissionNode;
        this.items = items;
        this.displayItem = displayItem;
    }

    public Kit(Map<String, Object> data) {
        this.id = ((Long) data.get("ID")).intValue();
        this.className = (String) data.get("ClassName");
        this.permission = (String) data.get("Permission");
        this.items = new ItemStack[((Long) data.get("InventorySize")).intValue()];
        try {
            this.displayItem = KitManager.itemFrom64((String) data.get("DisplayItem"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, Object> items = (Map<String, Object>) data.get("Items");
        items.forEach((slot, item) -> {
            int slo = Integer.parseInt(slot);
            ItemStack itemStack;
            try {
                itemStack = KitManager.itemFrom64((String) item);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            this.items[slo] = itemStack;
        });
    }

    public Map<String, Object> save() {
        Map<String, Object> saved = new HashMap<>(), items = new HashMap<>();

        saved.put("ID", this.id);
        saved.put("ClassName", className);
        saved.put("Permission", permission);
        saved.put("InventorySize", this.items.length);
        saved.put("DisplayItem", KitManager.itemTo64(this.displayItem));

        for (int i = 0; i < this.items.length; i++) {
            if (this.items[i] == null) {
                continue;
            }

            items.put(String.valueOf(i), KitManager.itemTo64(this.items[i]));

        }

        saved.put("Items", items);

        return saved;
    }

    public void setItem(int slot, ItemStack item) {
        this.items[slot] = item;
    }

    public Inventory getClassItems() {
        Inventory i = Bukkit.getServer().createInventory(null, items.length, className);

        i.setContents(items);

        return i;
    }

    public Inventory getClassEdit() {
        Inventory i = Bukkit.getServer().createInventory(null, items.length, className + " Edit");

        i.setContents(items);

        return i;
    }

    public void updateItems(Inventory updatedItems) {
        this.items = updatedItems.getContents();
    }

    public void addItems(Player p) {
        for (ItemStack item : items) {
            if (item == null) {
                continue;
            }
            p.getInventory().addItem(item.clone());
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Kit && ((Kit) obj).getId() == this.getId();
    }
}
