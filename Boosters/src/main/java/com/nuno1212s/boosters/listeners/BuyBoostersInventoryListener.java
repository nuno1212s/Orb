package com.nuno1212s.boosters.listeners;

import com.nuno1212s.boosters.inventories.BInventoryItem;
import com.nuno1212s.boosters.main.Main;
import com.nuno1212s.util.inventories.InventoryData;
import com.nuno1212s.util.inventories.InventoryItem;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Buy boosters inventory handler
 */
public class BuyBoostersInventoryListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        InventoryData sI = Main.getIns().getInventoryManager().getSellInventory();
        if (sI.equals(e.getInventory())) {
            if (e.getClick().isShiftClick()) {
                e.setResult(Event.Result.DENY);
            }
        } else {
            return;
        }
        if (sI.equals(e.getClickedInventory())) {
            e.setResult(Event.Result.DENY);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            InventoryItem item = sI.getItem(e.getSlot());

            if (item == null) {
                return;
            }

            if (item.hasItemFlag("PREVIOUS_PAGE")) {
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().openInventory(Main.getIns().getInventoryManager().buildLandingInventory());
                return;
            }

            e.getWhoClicked().closeInventory();
            e.getWhoClicked().openInventory(Main.getIns().getInventoryManager().buildBuyConfirmInventory((BInventoryItem) item));

        }
    }

}
