package com.nuno1212s.enderchest.listeners;

import com.nuno1212s.enderchest.main.Main;
import com.nuno1212s.enderchest.playerdata.EnderChestData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.EnderChest;

public class PlayerInteractListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getClickedBlock().getType() != Material.ENDER_CHEST) return;

        e.setCancelled(true);

        PlayerData d = MainData.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());

        if (!(d instanceof EnderChestData)) {

            return;
        }

        e.getPlayer().openInventory(Main.getIns().getEnderChestManager().getEnderChestFor(e.getPlayer(), (EnderChestData) d));

    }

}
