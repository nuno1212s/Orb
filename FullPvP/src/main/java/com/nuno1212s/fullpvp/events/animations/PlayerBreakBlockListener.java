package com.nuno1212s.fullpvp.events.animations;

import com.nuno1212s.fullpvp.main.Main;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Player break block listener
 */
public class PlayerBreakBlockListener implements Listener {

    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent e) {
        if (Main.getIns().getCrateManager().isCrateLocation(e.getBlock().getLocation())) {
            if (!e.getPlayer().hasPermission("crate.break")) {
                e.setCancelled(true);
            } else {
                Main.getIns().getCrateManager().removeCrateAtLocation(e.getBlock().getLocation());
                e.getPlayer().sendMessage(ChatColor.RED + "Removeste uma crate do bloco que partiste.");
            }
        }
    }

}
