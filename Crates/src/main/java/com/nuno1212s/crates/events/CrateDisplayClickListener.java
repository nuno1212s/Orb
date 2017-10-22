package com.nuno1212s.crates.events;

import com.nuno1212s.crates.Main;
import com.nuno1212s.crates.crates.Crate;
import com.nuno1212s.util.inventories.InventoryData;
import com.nuno1212s.util.inventories.InventoryItem;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CrateDisplayClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        InventoryData crateDisplay = Main.getIns().getCrateManager().getCrateDisplayInventory();
        if (crateDisplay.equals(e.getInventory())) {
            e.setResult(Event.Result.DENY);

            InventoryData inventory = crateDisplay;
            InventoryItem clickedItem = inventory.getItem(e.getSlot());

            if (clickedItem == null) {
                return;
            }

            if (clickedItem.hasItemFlag("RETURN")) {
                Crate c = Main.getIns().getCrateManager().getCrate("");
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().openInventory(c.getBuyKeyConfirmInventory());
            }


        }
    }

}
