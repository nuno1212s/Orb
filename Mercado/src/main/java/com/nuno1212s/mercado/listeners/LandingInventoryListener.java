package com.nuno1212s.mercado.listeners;

import com.nuno1212s.mercado.main.Main;
import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventories.InventoryItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

/**
 * Landing inventorylisteners listener
 */
public class LandingInventoryListener implements Listener {

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (Main.getIns().getMarketManager().getLandingInventoryData().equals(e.getInventory())) {
            e.setResult(Event.Result.DENY);
        }
    }

    @EventHandler
    public void onLandingClick(InventoryClickEvent e) {
        if (Main.getIns().getMarketManager().getLandingInventoryData().equals(e.getInventory())) {
            if (e.getClick().isShiftClick()) {
                e.setResult(Event.Result.DENY);
            }
        } else {
            return;
        }

        if (e.getClickedInventory() == null) {
            return;
        }

        if (e.getClickedInventory().getName().equals(e.getInventory().getName())) {
            if (e.getCurrentItem() == null) {
                return;
            }

            e.setResult(Event.Result.DENY);

            InventoryData data = Main.getIns().getMarketManager().getLandingInventoryData();
            InventoryItem item = data.getItem(e.getSlot());

            if (item == null) {
                return;
            }

            if (item.hasItemFlag("SEE_LISTED")) {
                e.getWhoClicked().closeInventory();
                Main.getIns().getMarketManager().openInventory((Player) e.getWhoClicked(), 1);
            } else if (item.hasItemFlag("SEE_OWN")) {
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().openInventory(Main.getIns().getMarketManager().getOwnItemInventory(e.getWhoClicked().getUniqueId(), 1));
            } else if (item.hasItemFlag("SELL_ITEM")) {
                e.getWhoClicked().closeInventory();
                InventoryData sellInventory = Main.getIns().getMarketManager().getSellInventory();
                Inventory inventory = sellInventory.buildInventory();
                InventoryItem currency_type = sellInventory.getItemWithFlag("CURRENCY_TYPE");
                inventory.setItem(currency_type.getSlot(), Main.getIns().getMarketManager().getCoinsItems().clone());
                e.getWhoClicked().openInventory(inventory);
            }

        }

    }

}
