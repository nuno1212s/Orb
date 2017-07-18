package com.nuno1212s.ferreiro.listeners;

import com.nuno1212s.ferreiro.main.Main;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/**
 * Confirm inventory listener
 */
public class ConfirmInventoryListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getName().equalsIgnoreCase(ChatColor.RED + "Confirm the repair")) {
            e.setResult(Event.Result.DENY);
            if (e.getClickedInventory().getName().equalsIgnoreCase(ChatColor.RED + "Confirm the repair")) {
                if (e.getSlot() == 11) {
                    e.getWhoClicked().closeInventory();
                    Main.getIns().getInventory(e.getWhoClicked().getUniqueId()).getC().callback();
                } else if (e.getSlot() == 15) {
                    e.getWhoClicked().closeInventory();
                    Main.getIns().removeInventory(e.getWhoClicked().getUniqueId());
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getName().equalsIgnoreCase(ChatColor.RED + "Confirm the repair")) {
            Main.getIns().removeInventory(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getInventory().getName().equalsIgnoreCase(ChatColor.RED + "Confirm the repair")) {
            e.setResult(Event.Result.DENY);
        }
    }

}
