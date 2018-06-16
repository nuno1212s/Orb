package com.nuno1212s.classes.classmanager;

import com.nuno1212s.classes.player.KitPlayer;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.ItemUtils;
import com.nuno1212s.util.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
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
    private long delay;

    public Kit(int id, String className, String permissionNode, ItemStack[] items, long delay) {
        this.id = id;
        this.className = className;
        this.permission = permissionNode;
        this.items = items;
        this.delay = delay;
    }

    public Kit(Map<String, Object> data) {
        this.id = ((Long) data.get("ID")).intValue();
        this.className = (String) data.get("ClassName");
        this.permission = (String) data.get("Permission");
        this.items = new ItemStack[((Long) data.get("InventorySize")).intValue()];
        this.delay = ((Long) data.get("Delay"));
        Map<String, Object> items = (Map<String, Object>) data.get("Items");
        items.forEach((slot, item) -> {
            int slo = Integer.parseInt(slot);
            ItemStack itemStack;
            try {
                itemStack = ItemUtils.itemFrom64((String) item);
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
        saved.put("Delay", this.delay);

        for (int i = 0; i < this.items.length; i++) {
            if (this.items[i] == null) {
                continue;
            }

            items.put(String.valueOf(i), ItemUtils.itemTo64(this.items[i]));

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

    public boolean canUseKit(Player p, PlayerData d) {

        if (!this.getPermission().equalsIgnoreCase("") && p.hasPermission(this.getPermission())) {
            return true;
        }

        return d instanceof KitPlayer && ((KitPlayer) d).ownsKit(getId());
    }

    private void addItems(Player p) {
        for (ItemStack item : items) {
            if (item == null) {
                continue;
            }
            p.getInventory().addItem(item.clone());
        }
    }

    public void giveKitTo(Player player) {
        if (!getPermission().equalsIgnoreCase("")) {
            if (!player.hasPermission(getPermission())) {
                MainData.getIns().getMessageManager().getMessage("NO_KIT_PERMISSION").sendTo(player);
                return;
            }
        }

        /*
        CHECK IF THIS SERVER SUPPORTS KIT USAGES
         */
        PlayerData player1 = MainData.getIns().getPlayerManager().getPlayer(player.getUniqueId());
        if (player1 instanceof KitPlayer) {
            KitPlayer player11 = (KitPlayer) player1;

            if (!player11.canUseKit(getId(), this.getDelay())) {
                MainData.getIns().getMessageManager().getMessage("CANT_USE_KIT")
                        .format("%time%", new TimeUtil("DD days:HH hours:MM minutes:SS seconds")
                                .toTime(player11.timeUntilUsage(getId(), this.getDelay())))
                        .sendTo(player);
                return;
            }

            ((KitPlayer) player1).registerKitUsage(getId(), System.currentTimeMillis());
        }

        addItems(player);
        MainData.getIns().getMessageManager().getMessage("RECEIVED_KIT").format("%kitName%", getClassName()).sendTo(player);

    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Kit && ((Kit) obj).getId() == this.getId();
    }

}
