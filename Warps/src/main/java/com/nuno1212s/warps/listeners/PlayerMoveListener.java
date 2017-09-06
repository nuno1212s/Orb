package com.nuno1212s.warps.listeners;

import com.nuno1212s.main.MainData;
import com.nuno1212s.warps.main.Main;
import com.nuno1212s.warps.warpmanager.WarpTimer;
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
            WarpTimer warpTimer = Main.getIns().getWarpManager().getWarpTimer();
            if (warpTimer.isWarping(e.getPlayer().getUniqueId())) {
                warpTimer.cancelWarp(e.getPlayer().getUniqueId());
                MainData.getIns().getMessageManager().getMessage("WARPS_CANCELLED_MOVED").sendTo(e.getPlayer());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent e) {
        WarpTimer warpTimer = Main.getIns().getWarpManager().getWarpTimer();
        if (warpTimer.isWarping(e.getPlayer().getUniqueId())) {
            warpTimer.cancelWarp(e.getPlayer().getUniqueId());
            MainData.getIns().getMessageManager().getMessage("WARPS_CANCELLED_MOVED").sendTo(e.getPlayer());
        }
    }

}
