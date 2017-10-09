package com.nuno1212s.enderchest.listeners;

import com.nuno1212s.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.enderchest.playerdata.EnderChestData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryCloseListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getName().equalsIgnoreCase(Main.getIns().getEnderChestManager().getInventoryName())) {
            ItemStack[] contents = e.getInventory().getContents();

            PlayerData player = MainData.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
            if (player instanceof EnderChestData) {
                ((EnderChestData) player).updateEnderChestData(contents);
            }

        }
    }

}
