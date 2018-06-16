package com.nuno1212s.mercadonegro.inventories;

import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventories.InventoryItem;
import com.nuno1212s.mercadonegro.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.io.File;

public class CInventoryData extends InventoryData<CInventoryItem> {

    public CInventoryData(File inventoryFile) {
        super(inventoryFile, CInventoryItem.class, true);
    }

    @Override
    public Inventory buildInventory() {
        Inventory i = Bukkit.getServer().createInventory(null, getInventorySize(), getInventoryName());

        for (InventoryItem inventoryItem : this.getItems()) {
            if (inventoryItem instanceof CInventoryItem) {
                i.setItem(inventoryItem.getSlot(), ((CInventoryItem) inventoryItem).getDisplayItem());
            } else {
                i.setItem(inventoryItem.getSlot(), inventoryItem.getItem());
            }
        }

        return i;
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) {
            return;
        }

        e.setResult(Event.Result.DENY);

        CInventoryItem item = getItem(e.getSlot());

        if (item == null) {
            return;
        }

        e.getWhoClicked().closeInventory();
        e.getWhoClicked().openInventory(Main.getIns().getInventoryManager().buildConfirmInventory(this, item));
    }
}
