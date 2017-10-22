package com.nuno1212s.classes.events;

import com.nuno1212s.classes.Main;
import com.nuno1212s.classes.classmanager.Kit;
import com.nuno1212s.classes.inventories.KInventoryData;
import com.nuno1212s.classes.inventories.KInventoryItem;
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
        if (Main.getIns().getKitManager().getByInventory(e.getInventory()) != null) {
            e.setResult(Event.Result.DENY);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        KInventoryData byInventory = Main.getIns().getKitManager().getByInventory(e.getInventory());
        if (byInventory != null) {
            if (e.getClick().isShiftClick()) {
                e.setResult(Event.Result.DENY);
            }

            if (e.getClickedInventory() != null && byInventory.equals(e.getClickedInventory())) {
                return;
            }

            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            if (byInventory.equals(e.getClickedInventory())) {
                e.setResult(Event.Result.DENY);

                KInventoryItem item = (KInventoryItem) byInventory.getItem(e.getSlot());

                if (item == null) {
                    return;
                }

                if (item.isKit()) {


                    Kit kit = item.getKit();

                    if (e.getClick().isLeftClick()) {

                        kit.giveKitTo((Player) e.getWhoClicked());
                        e.getClickedInventory().setContents(byInventory.buildInventory((Player) e.getWhoClicked()).getContents());

                    } else if (e.getClick().isRightClick()) {

                        e.getWhoClicked().closeInventory();
                        e.getWhoClicked().openInventory(kit.getClassItems());

                    }

                    return;
                }

                String connectingInventory = item.getConnectingInventory();

                if (connectingInventory == null) {
                    return;
                }

                e.getWhoClicked().closeInventory();

                KInventoryData inventoryByID = Main.getIns().getKitManager().getInventoryByID(connectingInventory);

                if (inventoryByID == null) {
                    return;
                }

                e.getWhoClicked().openInventory(inventoryByID.buildInventory((Player) e.getWhoClicked()));

            }

        }
    }


}
