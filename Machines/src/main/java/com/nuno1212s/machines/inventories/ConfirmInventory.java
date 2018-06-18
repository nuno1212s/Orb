package com.nuno1212s.machines.inventories;

import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventories.InventoryItem;
import com.nuno1212s.machines.machinemanager.Machine;
import com.nuno1212s.machines.machinemanager.MachineConfiguration;
import com.nuno1212s.machines.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.Callback;
import com.nuno1212s.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConfirmInventory extends InventoryData<InventoryItem> {

    private Map<UUID, Pair<Callback<InventoryClickEvent>, Callback<InventoryClickEvent>>> callbacks;

    public ConfirmInventory(File f) {
        super(f, null, true);

        callbacks = new HashMap<>();
    }

    public Inventory buildInventory(Player p, Machine m, Callback<InventoryClickEvent> onAccept, Callback<InventoryClickEvent> onCancel) {

        Inventory i = Bukkit.getServer().createInventory(null, this.getInventorySize(), this.getInventoryName());

        for (InventoryItem item : items) {

            if (item.hasItemFlag("DISPLAY")) {
                i.setItem(item.getSlot(), m.getItem());
                continue;
            }

            i.setItem(item.getSlot(), m.writeToItem(item.getItem()));
        }

        callbacks.put(p.getUniqueId(), new Pair<>(onAccept, onCancel));

        return i;
    }

    public Inventory buildInventory(Player p, MachineConfiguration m, Callback<InventoryClickEvent> onAccept, Callback<InventoryClickEvent> onCancel) {

        Inventory i = Bukkit.getServer().createInventory(null, this.getInventorySize(), this.getInventoryName());

        for (InventoryItem item : items) {

            if (item.hasItemFlag("DISPLAY")) {
                i.setItem(item.getSlot(), m.getItem());
                continue;
            }

            i.setItem(item.getSlot(), m.intoItem(item.getItem()));
        }

        callbacks.put(p.getUniqueId(), new Pair<>(onAccept, onCancel));

        return i;
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        e.setResult(Event.Result.DENY);

        InventoryItem item = getItem(e.getSlot());

        if (item == null) {
            return;
        }

        if (item.hasItemFlag("CONFIRM")) {

            this.callbacks.get(e.getWhoClicked().getUniqueId()).getKey().callback(e);
            this.callbacks.remove(e.getWhoClicked().getUniqueId());

        } else if (item.hasItemFlag("CANCEL")) {

            this.callbacks.get(e.getWhoClicked().getUniqueId()).getValue().callback(e);
            this.callbacks.remove(e.getWhoClicked().getUniqueId());

        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getName().equalsIgnoreCase(getInventoryName())) {

            if (this.callbacks.containsKey(e.getPlayer().getUniqueId())) {
                this.callbacks.remove(e.getPlayer().getUniqueId());
            }

        }
    }
}
