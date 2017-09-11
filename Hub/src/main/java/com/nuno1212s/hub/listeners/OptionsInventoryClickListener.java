package com.nuno1212s.hub.listeners;

import com.nuno1212s.hub.main.Main;
import com.nuno1212s.hub.playerdata.HPlayerData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.inventories.InventoryData;
import com.nuno1212s.util.inventories.InventoryItem;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

/**
 * Options inventory click handler
 */
public class OptionsInventoryClickListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onDrag(InventoryDragEvent e) {
        if (Main.getIns().getPlayerOptionsManager().getOptionsInventory().equals(e.getInventory())) {
            e.setResult(Event.Result.DENY);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onClick(InventoryClickEvent e) {
        InventoryData optionsInventory = Main.getIns().getPlayerOptionsManager().getOptionsInventory();
        if (optionsInventory.equals(e.getInventory())) {
            if (e.getClick().isShiftClick()) {
                e.setResult(Event.Result.DENY);
            }
        } else {
            return;
        }

        if (optionsInventory.equals(e.getClickedInventory())) {
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

            e.setResult(Event.Result.DENY);

            InventoryItem item = optionsInventory.getItem(e.getSlot());
            if (item != null) {
                if (item.hasItemFlag("TELL_TOGGLE")) {
                    HPlayerData d = (HPlayerData) MainData.getIns().getPlayerManager().getPlayer(e.getWhoClicked().getUniqueId());
                    d.setTell(!d.isTell());

                    Main.getIns().getRedisHandler().publishTellUpdate(d);
                    Inventory playerInventory = Main.getIns().getPlayerOptionsManager().getInventoryForPlayer(d);
                    e.getClickedInventory().setContents(playerInventory.getContents());
                } else if (item.hasItemFlag("CHAT_TOGGLE")) {
                    HPlayerData d = (HPlayerData) MainData.getIns().getPlayerManager().getPlayer(e.getWhoClicked().getUniqueId());
                    d.setChatEnabled(!d.isChatEnabled());

                    Inventory playerInventory = Main.getIns().getPlayerOptionsManager().getInventoryForPlayer(d);
                    e.getClickedInventory().setContents(playerInventory.getContents());
                }
            }

        }

    }

}
