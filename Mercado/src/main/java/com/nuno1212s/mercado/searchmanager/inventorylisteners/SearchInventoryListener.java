package com.nuno1212s.mercado.searchmanager.inventorylisteners;

import com.nuno1212s.mercado.main.Main;
import com.nuno1212s.mercado.searchmanager.SearchParameter;
import com.nuno1212s.mercado.searchmanager.SearchParameterBuilder;
import com.nuno1212s.mercado.searchmanager.SearchParameterManager;
import com.nuno1212s.mercado.util.SInventoryListener;
import com.nuno1212s.mercado.util.inventories.InventoryItem;
import com.nuno1212s.mercado.util.searchinventories.SInventoryData;
import com.nuno1212s.mercado.util.searchinventories.SInventoryItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Search inventory listener
 */
public class SearchInventoryListener extends SInventoryListener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        SearchParameterManager searchManager = Main.getIns().getMarketManager().getSearchManager();
        SInventoryData inventoryByName = searchManager.getInventoryByName(e.getInventory().getName());

        if (inventoryByName != null) {
            if (e.getClick().isShiftClick()) {
                e.setResult(Event.Result.DENY);
            }
        } else {
            return;
        }

        if (e.getClickedInventory() == null) {
            return;
        }

        if (inventoryByName.equals(e.getClickedInventory())) {
            e.setResult(Event.Result.DENY);

            InventoryItem item = inventoryByName.getItem(e.getSlot());
            if (item != null) {
                if (item instanceof SInventoryItem) {
                    if (((SInventoryItem) item).hasConnectingInventory()) {
                        String connectingInventory = ((SInventoryItem) item).getConnectingInventory();
                        SInventoryData inventory = searchManager.getInventory(connectingInventory);
                        addCloseException(e.getWhoClicked().getUniqueId());
                        e.getWhoClicked().closeInventory();
                        e.getWhoClicked().openInventory(searchManager.getSearchParameterInventory(e.getWhoClicked().getUniqueId(), inventory));

                    } else if (item.hasItemFlag("SEARCH")) {
                        addCloseException(e.getWhoClicked().getUniqueId());
                        e.getWhoClicked().closeInventory();
                        Main.getIns().getMarketManager().openInventory((Player) e.getWhoClicked(), 1);
                    } else {
                        SearchParameter searchParameter = ((SInventoryItem) item).getSearchParameter();
                        SearchParameterBuilder searchParameterBuilder = searchManager.getSearchParameterBuilder(e.getWhoClicked().getUniqueId());
                        System.out.println(searchParameter);
                        System.out.println(item);
                        if (searchParameterBuilder.containsParameter(searchParameter)) {
                            searchParameterBuilder.removeSearchParameter(searchParameter);
                        } else {
                            searchParameterBuilder.addSearchParameter(searchParameter);
                        }
                        e.getClickedInventory().setContents(searchManager.getSearchParameterInventory(e.getWhoClicked().getUniqueId(), inventoryByName).getContents());

                    }

                }


            }

        }

    }

}
