package com.nuno1212s.mercado.listeners;

import com.nuno1212s.main.MainData;
import com.nuno1212s.mercado.main.Main;
import com.nuno1212s.mercado.marketmanager.Item;
import com.nuno1212s.mercado.util.InventoryListener;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import com.nuno1212s.util.inventories.InventoryItem;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.UUID;

/**
 * Listens to inventorylisteners events
 */
public class OwnInventoryListener extends InventoryListener {

    public OwnInventoryListener() {
        super(Main.getIns().getMarketManager().getOwnInventory());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (getInventory().equals(e.getInventory())) {
            if (e.getClick().isShiftClick()) {
                e.setResult(Event.Result.DENY);
            }
        } else {
            return;
        }

        if (e.getClickedInventory() == null) {
            return;
        }

        if (getInventory().equals(e.getClickedInventory())) {
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            e.setResult(Event.Result.DENY);

            InventoryItem invItem = getInventory().getItem(e.getSlot());
            UUID uuid = e.getWhoClicked().getUniqueId();
            if (invItem != null) {
                if (invItem.hasItemFlag("PREVIOUS_PAGE")) {
                    addCloseException(uuid);
                    e.getWhoClicked().closeInventory();
                    if (getPageForPlayer(uuid) == 1) {
                        e.getWhoClicked().openInventory(Main.getIns().getMarketManager().getLandingInventory());
                    } else {
                        e.getWhoClicked().openInventory(Main.getIns().getMarketManager().getOwnItemInventory(uuid, getPageForPlayer(uuid) - 1));
                    }
                } else if (invItem.hasItemFlag("NEXT_PAGE")) {
                    addCloseException(uuid);
                    e.getWhoClicked().closeInventory();
                    e.getWhoClicked().openInventory(Main.getIns().getMarketManager().getOwnItemInventory(uuid, getPageForPlayer(uuid) + 1));
                }
                return;
            }

            NBTCompound compound = new NBTCompound(e.getCurrentItem());

            if (compound.getValues().containsKey("ItemID")) {
                String itemID = (String) compound.getValues().get("ItemID");

                Item item = Main.getIns().getMarketManager().getItem(itemID);
                if (item == null) {
                    return;
                }

                if (e.getClick() == ClickType.SHIFT_LEFT) {
                    if (item.isSold()) {
                        MainData.getIns().getMessageManager().getMessage("CANNOT_REMOVE_SOLD_ITEM").sendTo(e.getWhoClicked());
                        return;
                    }

                    e.getWhoClicked().getInventory().addItem(item.getItem().clone());
                    Main.getIns().getMarketManager().removeItem(item.getItemID());
                    MainData.getIns().getMessageManager().getMessage("REMOVED_ITEM").sendTo(e.getWhoClicked());

                    e.getClickedInventory().setContents(Main.getIns().getMarketManager().getOwnItemInventory(uuid, getPageForPlayer(uuid)).getContents());

                }

            }

        }

    }

}
