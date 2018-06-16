package com.nuno1212s.hub.util;

import com.nuno1212s.hub.main.Main;
import com.nuno1212s.inventories.InventoryData;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;

/**
 * Inventory data for the server selector
 */
public class HInventoryData extends InventoryData<HInventoryItem> {

    public HInventoryData(File file) {
        super(file, HInventoryItem.class, true);
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        e.setResult(Event.Result.DENY);

        HInventoryItem item = getItem(e.getSlot());

        if (item == null) return;

        String connectingServer = item.getConnectingServer();

        if (connectingServer == null) return;

        e.getWhoClicked().closeInventory();
        item.sendPlayerToServer((Player) e.getWhoClicked());
    }

}
