package com.nuno1212s.mercado.listeners;

import com.nuno1212s.mercado.main.Main;
import com.nuno1212s.mercado.marketmanager.MarketManager;
import com.nuno1212s.mercado.util.InventoryListener;
import com.nuno1212s.mercado.util.inventories.InventoryItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Handles confirm inventory clicks
 */
public class ConfirmInventoryListener extends InventoryListener {

    public ConfirmInventoryListener() {
        super(Main.getIns().getMarketManager().getConfirmInventoryData());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        MarketManager marketManager = Main.getIns().getMarketManager();
        if (marketManager.getConfirmInventoryData().equals(e.getInventory())) {
            if (e.getClick().isShiftClick()) {
                e.setResult(Event.Result.DENY);
            }
        }

        if (e.getInventory().getName().equals(e.getClickedInventory().getName())) {
            if (e.getCurrentItem() == null) {
                return;
            }

            e.setResult(Event.Result.DENY);

            InventoryItem item = marketManager.getConfirmInventoryData().getItem(e.getSlot());
            if (item.hasItemFlag("CONFIRM")) {

            } else if (item.hasItemFlag("CANCEL")) {
                e.getWhoClicked().closeInventory();
                addCloseException(e.getWhoClicked().getUniqueId());
                marketManager.openInventory((Player) e.getWhoClicked(), marketManager.getPage(e.getWhoClicked().getUniqueId()));
            }
        }
    }

}
