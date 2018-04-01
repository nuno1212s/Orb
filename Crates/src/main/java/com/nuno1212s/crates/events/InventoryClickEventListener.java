package com.nuno1212s.crates.events;

import com.nuno1212s.crates.crates.Crate;
import com.nuno1212s.crates.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.inventories.InventoryData;
import com.nuno1212s.util.inventories.InventoryItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Inventory click event
 */
public class InventoryClickEventListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) {
            return;
        }

        if (Main.getIns().getCrateManager().getAnimationManager().isInventoryBeingUsed(e.getInventory())) {
            e.setResult(Event.Result.DENY);
            return;
        }

        InventoryData confirmInventory = Main.getIns().getCrateManager().getConfirmInventory();
        if (confirmInventory.equals(e.getInventory())) {
            e.setResult(Event.Result.DENY);

            if (confirmInventory.equals(e.getClickedInventory())) {
                InventoryItem item = confirmInventory.getItem(e.getSlot());
                if (item == null) {
                    return;
                }

                if (item.hasItemFlag("CONFIRM")) {
                    Crate c = Main.getIns().getCrateManager()
                            .getCrateForKey(e.getClickedInventory()
                                    .getItem(confirmInventory.getItemWithFlag("SHOW_ITEM").getSlot()));
                    PlayerData d = MainData.getIns().getPlayerManager().getPlayer(e.getWhoClicked().getUniqueId());
                    if (c.isCash()) {
                        if (d.getCash() >= c.getKeyCost()) {
                            d.setCash(d.getCash() - c.getKeyCost());
                            e.getWhoClicked().closeInventory();
                            c.openCase((Player) e.getWhoClicked());
                            MainData.getIns().getEventCaller().callUpdateInformationEvent(d);
                        } else {
                            MainData.getIns().getMessageManager().getMessage("NO_CASH").sendTo(e.getWhoClicked());
                        }
                    } else {
                        if (MainData.getIns().getServerCurrencyHandler().removeCurrency(d, c.getKeyCost())) {
                            e.getWhoClicked().closeInventory();
                            c.openCase((Player) e.getWhoClicked());
                            MainData.getIns().getEventCaller().callUpdateInformationEvent(d);
                        } else {
                            MainData.getIns().getMessageManager().getMessage("NO_COINS").sendTo(e.getWhoClicked());
                        }
                    }
                } else if (item.hasItemFlag("CANCEL")) {
                    e.getWhoClicked().closeInventory();
                } else if (item.hasItemFlag("DISPLAY_ITEMS")) {
                    e.getWhoClicked().closeInventory();

                    Crate c = Main.getIns().getCrateManager()
                            .getCrateForKey(e.getClickedInventory()
                                    .getItem(confirmInventory.getItemWithFlag("SHOW_ITEM").getSlot()));

                    e.getWhoClicked().openInventory(c.buildCrateDisplay());
                }
            }

        }
    }

}
