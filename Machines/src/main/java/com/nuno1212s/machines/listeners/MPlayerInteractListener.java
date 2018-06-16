package com.nuno1212s.machines.listeners;

import com.nuno1212s.machines.machinemanager.Machine;
import com.nuno1212s.machines.main.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class MPlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Machine m = Main.getIns().getMachineManager().getMachineAtLocation(e.getClickedBlock().getLocation());

        if (m == null) {
            return;
        }

        //TODO: Handle machine inventory?


    }

}
