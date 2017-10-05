package com.nuno1212s.hub.listeners;

import com.nuno1212s.hub.main.Main;
import com.nuno1212s.hub.playerdata.HPlayerData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.inventories.InventoryData;
import com.nuno1212s.util.inventories.InventoryItem;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Player interact listener
 */
public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getItem() == null || e.getItem().getType() == Material.AIR) return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR) return;

        e.setCancelled(true);

        int slot = e.getPlayer().getInventory().getHeldItemSlot();

        InventoryItem item = Main.getIns().getHotbarManager().getItem(slot);

        if (item == null) {
            return;
        }

        if (item.hasItemFlag("SERVER_SELECTOR")) {
            Inventory mainInventory = Main.getIns().getServerSelectorManager().getMainInventory();
            if (mainInventory != null)
                e.getPlayer().openInventory(mainInventory);
        } else if (item.hasItemFlag("OPTIONS")) {
            HPlayerData data = (HPlayerData) MainData.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
            e.getPlayer().openInventory(Main.getIns().getPlayerOptionsManager().getInventoryForPlayer(data));
        } else if (item.hasItemFlag("HIDE_PLAYERS")) {
            HPlayerData data = (HPlayerData) MainData.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
            data.setPlayerShown(!data.isPlayerShown());

            if (data.isPlayerShown()) {
                MainData.getIns().getMessageManager().getMessage("TOGGLED_PLAYERS_ON").sendTo(e.getPlayer());
            } else {
                MainData.getIns().getMessageManager().getMessage("TOGGLED_PLAYERS_OFF").sendTo(e.getPlayer());
            }

            Main.getIns().getPlayerToggleManager().updatePlayer(data, e.getPlayer());
            Map<Integer, ItemStack> items = Main.getIns().getHotbarManager().getItems(data);
            e.getPlayer().getInventory().setItem(item.getSlot(), items.get((item.getSlot())));
        } else if (item.hasItemFlag("INBOX")) {
            Inventory inventory = com.nuno1212s.npcinbox.main.Main.getIns().getInventoryManager().buildRewardInventoryForPlayer(MainData.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId()));
            e.getPlayer().openInventory(inventory);
        }

    }

}
