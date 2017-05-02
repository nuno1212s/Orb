package com.nuno1212s.classes.events;

import com.nuno1212s.classes.classmanager.Class;
import com.nuno1212s.classes.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * Class Edit inventory listener
 *
 * Handles classes
 */
public class ClassEditInventoryListener implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Class classEdit = Main.getIns().getClassManager().getClassEdit(e.getInventory().getName());
        if (classEdit != null) {
            classEdit.updateItems(e.getInventory());
        }
    }

}
