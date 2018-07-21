package com.nuno1212s.machines.listeners;

import com.nuno1212s.machines.machinemanager.Machine;
import com.nuno1212s.machines.main.Main;
import com.nuno1212s.main.MainData;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class MachineInteractListener implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (e.getItem() != null && e.getItem().getType() != Material.AIR) return;

        Machine m = Main.getIns().getMachineManager().getMachineAtLocation(e.getClickedBlock().getLocation());

        if (m == null) {
            return;
        }

        if (!m.getOwner().equals(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);

            MainData.getIns().getMessageManager().getMessage("NOT_MACHINE_OWNER").sendTo(e.getPlayer());

            return;
        }

        e.setCancelled(true);
        Inventory inventoryForMachine = Main.getIns().getInventoryManager().getInventoryForMachine(m);

        if (inventoryForMachine != null) {
            e.getPlayer().openInventory(inventoryForMachine);
        }

    }

}
