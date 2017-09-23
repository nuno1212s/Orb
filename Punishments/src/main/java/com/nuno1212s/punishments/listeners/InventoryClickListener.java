package com.nuno1212s.punishments.listeners;

import com.nuno1212s.punishments.main.Main;
import com.nuno1212s.punishments.util.PInventoryItem;
import com.nuno1212s.util.inventories.InventoryData;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InventoryClickListener implements Listener {

    private List<UUID> notRemove = new ArrayList<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        InventoryData inventoryFromInventory = Main.getIns().getInventoryManager().getInventoryFromInventory(e.getClickedInventory());
        if (inventoryFromInventory != null) {
            if (!e.getClick().isShiftClick()) {
                e.setResult(Event.Result.DENY);
            }
        } else {
            return;
        }

        if (inventoryFromInventory.equals(e.getClickedInventory())) {
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

            e.setResult(Event.Result.DENY);

            PInventoryItem item = (PInventoryItem) inventoryFromInventory.getItem(e.getSlot());

            if (item == null) {
                return;
            }

            if (item.getConnectingInventory() != null) {
                notRemove.add(e.getWhoClicked().getUniqueId());
                e.getWhoClicked().closeInventory();
                InventoryData inventory = Main.getIns().getInventoryManager().getInventoryWithID(item.getConnectingInventory());

                if (inventory == null) {
                    System.out.println("WTF");
                    return;
                }

                e.getWhoClicked().openInventory(inventory.buildInventory());
                return;
            }

            item.applyToPlayer(Main.getIns().getInventoryManager().getTargetForPlayer(e.getWhoClicked().getUniqueId()));

            e.getWhoClicked().closeInventory();

        }

    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (Main.getIns().getInventoryManager().getInventoryFromInventory(e.getInventory()) != null
                && !notRemove.contains(e.getPlayer().getUniqueId())) {
            Main.getIns().getInventoryManager().removeTargetForPlayer(e.getPlayer().getUniqueId());
            return;
        }
        notRemove.remove(e.getPlayer().getUniqueId());
    }

}
