package com.nuno1212s.fullpvp.events.animations;

import com.nuno1212s.fullpvp.crates.Crate;
import com.nuno1212s.fullpvp.crates.CrateManager;
import com.nuno1212s.fullpvp.main.Main;
import com.nuno1212s.main.MainData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Listens for player interact events
 */
public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            CrateManager crateManager = Main.getIns().getCrateManager();
            Crate crateAtLocation = crateManager.getCrateAtLocation(e.getClickedBlock().getLocation());
            if (crateAtLocation == null) {
                return;
            } else {
                e.setCancelled(true);
                if (crateManager.canOpen(e.getPlayer(), crateAtLocation)) {
                    crateAtLocation.openCase(e.getPlayer());
                    MainData.getIns().getMessageManager().getMessage("OPENING_CRATE")
                            .format("%crateName%", crateAtLocation.getCrateName()).sendTo(e.getPlayer());
                } else {
                    MainData.getIns().getMessageManager().getMessage("NO_KEY_FOR_CRATE")
                            .format("%crateName%", crateAtLocation.getCrateName()).sendTo(e.getPlayer());
                }
            }
        }
    }

}
