package com.nuno1212s.mercado.listeners;

import com.nuno1212s.mercado.main.Main;
import com.nuno1212s.mercado.marketmanager.Item;
import com.nuno1212s.mercado.marketmanager.MarketManager;
import com.nuno1212s.mercado.util.InventoryListener;
import com.nuno1212s.mercado.util.inventories.InventoryData;
import com.nuno1212s.mercado.util.inventories.InventoryItem;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Map;

/**
 * Buying inventory listener.
 */
public class BuyingInventoryListener extends InventoryListener {

    public BuyingInventoryListener() {
        super(Main.getIns().getMarketManager().getMainInventoryData());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        MarketManager marketManager = Main.getIns().getMarketManager();
        if (marketManager.getMainInventoryData().equals(e.getInventory())) {
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

            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            e.setResult(Event.Result.DENY);

            InventoryItem item = marketManager.getMainInventoryData().getItem(e.getSlot());
            if (item == null) {

                //The item is a buying item, not a default inventory item
                NBTCompound itemData = new NBTCompound(e.getCurrentItem());
                Map<String, Object> values = itemData.getValues();
                if (values.containsKey("ItemID")) {
                    Item i = marketManager.getItem((String) values.get("ItemID"));
                    if (i == null) {
                        return;
                    }


                    /*if (i.getOwner().equals(e.getWhoClicked().getUniqueId())) {
                        MainData.getIns().getMessageManager().getMessage("CANTBUYOWNITEM").sendTo(e.getWhoClicked());
                        return;
                    }*/


                    InventoryData confirmInventoryData = marketManager.getConfirmInventoryData();
                    Inventory confirmInventory = confirmInventoryData.buildInventory();
                    InventoryItem show_item = confirmInventoryData.getItemWithFlag("SHOW_ITEM");

                    confirmInventory.setItem(show_item.getSlot(), e.getCurrentItem());

                    addCloseException(e.getWhoClicked().getUniqueId());
                    e.getWhoClicked().closeInventory();
                    e.getWhoClicked().openInventory(confirmInventory);

                }
                return;
            }

            if (item.hasItemFlag("PREVIOUS_PAGE")) {
                int page = marketManager.getPage(e.getWhoClicked().getUniqueId());
                if (page > 1) {
                    addCloseException(e.getWhoClicked().getUniqueId());
                    e.getWhoClicked().closeInventory();
                    marketManager.openInventory((Player) e.getWhoClicked(), page - 1);
                } else {
                    e.getWhoClicked().closeInventory();
                    e.getWhoClicked().openInventory(marketManager.getLandingInventory());

                }
            } else if (item.hasItemFlag("NEXT_PAGE")) {
                addCloseException(e.getWhoClicked().getUniqueId());
                e.getWhoClicked().closeInventory();
                marketManager.openInventory((Player) e.getWhoClicked(), marketManager.getPage(e.getWhoClicked().getUniqueId()) + 1);
            } else if (item.hasItemFlag("SEARCH")) {
                e.getWhoClicked().closeInventory();
                //TODO: Add search inventories and stuff
            }

        }

    }

}
