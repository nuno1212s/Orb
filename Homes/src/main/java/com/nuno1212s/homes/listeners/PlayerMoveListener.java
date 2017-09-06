package com.nuno1212s.homes.listeners;

import com.nuno1212s.homes.homemanager.HomeTimer;
import com.nuno1212s.homes.main.Main;
import com.nuno1212s.main.MainData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Handles player moving events
 */
public class PlayerMoveListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        if (e.getTo().getBlockX() != e.getFrom().getBlockX() || e.getTo().getBlockY() != e.getFrom().getBlockY() || e.getTo().getBlockZ() != e.getFrom().getBlockZ()) {
            HomeTimer warpTimer = Main.getIns().getHomeManager().getTimer();
            if (warpTimer.isTeleporting(e.getPlayer().getUniqueId())) {
                warpTimer.cancelTeleport(e.getPlayer().getUniqueId());
                MainData.getIns().getMessageManager().getMessage("WARPS_CANCELLED_MOVED").sendTo(e.getPlayer());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent e) {
        HomeTimer warpTimer = Main.getIns().getHomeManager().getTimer();
        if (warpTimer.isTeleporting(e.getPlayer().getUniqueId())) {
            warpTimer.cancelTeleport(e.getPlayer().getUniqueId());
            MainData.getIns().getMessageManager().getMessage("WARPS_CANCELLED_MOVED").sendTo(e.getPlayer());
        }
    }

}
