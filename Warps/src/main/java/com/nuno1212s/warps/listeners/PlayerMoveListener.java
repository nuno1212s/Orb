package com.nuno1212s.warps.listeners;

import com.nuno1212s.main.MainData;
import com.nuno1212s.warps.main.Main;
import com.nuno1212s.warps.timers.TeleportTimer;
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
            TeleportTimer timer = Main.getIns().getTeleportTimer();
            if (timer.isTeleporting(e.getPlayer().getUniqueId())) {
                timer.cancelTeleport(e.getPlayer().getUniqueId());
                MainData.getIns().getMessageManager().getMessage("TELEPORT_CANCELLED_MOVED").sendTo(e.getPlayer());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent e) {
        TeleportTimer timer = Main.getIns().getTeleportTimer();
        if (timer.isTeleporting(e.getPlayer().getUniqueId())) {
            timer.cancelTeleport(e.getPlayer().getUniqueId());
            MainData.getIns().getMessageManager().getMessage("TELEPORT_CANCELLED_MOVED").sendTo(e.getPlayer());
        }
    }

}
