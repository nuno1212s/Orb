package com.nuno1212s.machines.listeners;

import com.nuno1212s.machines.machinemanager.Machine;
import com.nuno1212s.machines.main.Main;
import com.nuno1212s.main.MainData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class MachineDestroyListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Machine machineAtLocation = Main.getIns().getMachineManager().getMachineAtLocation(e.getBlock().getLocation());

        if (machineAtLocation == null) {
            return;
        }

        if (!machineAtLocation.getOwner().equals(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            MainData.getIns().getMessageManager().getMessage("MACHINE_NOT_OWNER")
                    .sendTo(e.getPlayer());
            return;
        }

        e.setCancelled(true);

        if (machineAtLocation.decrementAmount()) {
            machineAtLocation.destroy(e.getPlayer());
        } else {
            e.getPlayer().getInventory().addItem(machineAtLocation.getItem());
        }
    }

}
