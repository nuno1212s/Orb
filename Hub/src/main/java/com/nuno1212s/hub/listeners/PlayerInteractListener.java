package com.nuno1212s.hub.listeners;

import com.nuno1212s.hub.main.Main;
import com.nuno1212s.util.inventories.InventoryItem;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Player interact listener
 */
public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getItem() == null || e.getItem().getType() == Material.AIR) return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getAction() != Action.RIGHT_CLICK_AIR) return;
        int slot = e.getPlayer().getInventory().getHeldItemSlot();

        InventoryItem item = Main.getIns().getHotbarManager().getItem(slot);

        if (item.hasItemFlag("SERVER_SELECTOR")) {
            e.getPlayer().openInventory(Main.getIns().getServerSelectorManager().getMainInventory());
        } else if (item.hasItemFlag("OPTIONS")) {

        }

    }

}
