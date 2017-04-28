package com.nuno1212s.rankup.events.animations;

import com.nuno1212s.rankup.crates.Crate;
import com.nuno1212s.rankup.crates.CrateManager;
import com.nuno1212s.rankup.main.Main;
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
        CrateManager crateManager = Main.getIns().getCrateManager();
        if (crateManager.isCrateKey(e.getItem())) {
            e.setCancelled(true);
        }
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
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
