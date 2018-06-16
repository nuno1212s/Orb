package com.nuno1212s.crates.events;

import com.nuno1212s.crates.Main;
import com.nuno1212s.crates.crates.Crate;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventories.InventoryItem;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class CrateDisplayClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        InventoryData crateDisplay = Main.getIns().getCrateManager().getCrateDisplayInventory();
        if (crateDisplay.equals(e.getInventory())) {
            e.setResult(Event.Result.DENY);

            InventoryItem clickedItem = crateDisplay.getItem(e.getSlot());

            if (clickedItem == null) {
                return;
            }

            if (clickedItem.hasItemFlag("RETURN")) {

                ItemStack i = e.getInventory().getItem(clickedItem.getSlot());

                NBTCompound nbt = new NBTCompound(i);

                Crate c = Main.getIns().getCrateManager().getCrate((String) nbt.getValues().get("Crate"));

                e.getWhoClicked().closeInventory();
                e.getWhoClicked().openInventory(c.getBuyKeyConfirmInventory());
            }


        }
    }

}
