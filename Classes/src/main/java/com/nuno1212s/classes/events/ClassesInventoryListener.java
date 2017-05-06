package com.nuno1212s.classes.events;

import com.nuno1212s.classes.Main;
import com.nuno1212s.classes.classmanager.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/**
 * Listens to the classes events
 */
public class ClassesInventoryListener implements Listener {

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getInventory().getName().equalsIgnoreCase(Main.getIns().getKitManager().getDisplayInventory().getInventoryName())) {
            e.setResult(Event.Result.DENY);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        String inventoryName = Main.getIns().getKitManager().getDisplayInventory().getInventoryName();
        if (e.getInventory().getName().equalsIgnoreCase(inventoryName)) {
            if (e.getClick().isShiftClick()) {
                e.setResult(Event.Result.DENY);
            }

            if (e.getClickedInventory() == null) {
                return;
            }

            if (e.getClickedInventory().getName().equalsIgnoreCase(inventoryName)) {
                e.setResult(Event.Result.DENY);
                if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
                    return;
                }
                if (e.getClick().isLeftClick()) {
                    Kit kitAtSlot = Main.getIns().getKitManager().getDisplayInventory().getKitAtSlot(e.getSlot());

                    if (kitAtSlot == null)  {
                        return;
                    }

                    kitAtSlot.giveKitTo((Player) e.getWhoClicked());
                } else if (e.getClick().isRightClick()) {
                    Kit kitAtSlot = Main.getIns().getKitManager().getDisplayInventory().getKitAtSlot(e.getSlot());

                    if (kitAtSlot == null) {
                        return;
                    }

                    e.getWhoClicked().closeInventory();
                    e.getWhoClicked().openInventory(kitAtSlot.getClassItems());
                }
            }
        }
    }

}
