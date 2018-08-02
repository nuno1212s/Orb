package com.nuno1212s.ferreiro.inventories;

import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventories.InventoryItem;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.Callback;
import com.nuno1212s.util.ItemUtils;
import com.nuno1212s.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class ConfirmInv extends InventoryData<InventoryItem> {

    private static Map<UUID, Callback> repairCosts = new WeakHashMap<>();

    public ConfirmInv(File f) {
        super(f, InventoryItem.class, true);
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        e.setResult(Event.Result.DENY);

        InventoryItem item = getItem(e.getSlot());

        if (item == null) {
            return;
        }

        if (item.hasItemFlag("CONFIRM")) {
            Callback cost = repairCosts.get(e.getWhoClicked().getUniqueId());

            cost.callback(null);

            e.getWhoClicked().closeInventory();
        } else if (item.hasItemFlag("CANCEL")) {
            repairCosts.remove(e.getWhoClicked().getUniqueId());

            e.getWhoClicked().closeInventory();
        }
    }

    public Inventory buildInventory(Player player, Pair<Integer, Boolean> cost, ItemStack item, Callback c) {

        Inventory i = Bukkit.getServer().createInventory(null, this.getInventorySize(), getInventoryName());

        Map<String, String> formats = new HashMap<>();

        if (cost.getValue())
            formats.put("%cost%", MainData.getIns().getMessageManager().getMessage("CASH_REPAIR")
                    .format("%cash%", cost.getKey()).toString());
        else
            formats.put("%cost%", MainData.getIns().getMessageManager().getMessage("COINS_REPAIR")
                    .format("%coins%", cost.getKey()).toString());

        for (InventoryItem inventoryItem : items) {

            if (inventoryItem.hasItemFlag("DISPLAYITEM")) {
                i.setItem(inventoryItem.getSlot(), item.clone());

                continue;
            }

            i.setItem(inventoryItem.getSlot(), ItemUtils.formatItem(inventoryItem.getItem().clone(), formats));
        }

        repairCosts.put(player.getUniqueId(), c);

        return i;
    }
}
