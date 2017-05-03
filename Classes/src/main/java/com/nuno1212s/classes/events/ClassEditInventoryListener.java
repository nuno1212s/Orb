package com.nuno1212s.classes.events;

import com.nuno1212s.classes.classmanager.Kit;
import com.nuno1212s.classes.Main;
import org.bukkit.ChatColor;
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
        Kit kitEdit = Main.getIns().getKitManager().getKitEdit(e.getInventory().getName());
        if (kitEdit != null) {
            kitEdit.updateItems(e.getInventory());
            e.getPlayer().sendMessage(ChatColor.RED + "Updated class " + kitEdit.getClassName());
        }
    }

}
