package com.nuno1212s.boosters.inventories;

import com.nuno1212s.boosters.main.Main;
import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventories.InventoryItem;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;

public class BInventoryData extends InventoryData<BInventoryItem> {


    public BInventoryData(File jsonFile) {
        super(jsonFile, BInventoryItem.class, true);
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        e.setResult(Event.Result.DENY);
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        InventoryItem item = getItem(e.getSlot());

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
