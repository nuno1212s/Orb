package com.nuno1212s.machines.listeners;

import com.nuno1212s.machines.machinemanager.Machine;
import com.nuno1212s.machines.machinemanager.MachineConfiguration;
import com.nuno1212s.machines.main.Main;
import com.nuno1212s.main.MainData;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class MachinePlaceListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {

        ItemStack itemInHand = e.getItemInHand();

        if (Main.getIns().getMachineManager().isMachine(itemInHand)) {

            MachineConfiguration configuration = MachineConfiguration.fromItem(e.getItemInHand());

            Machine machineAtLocation = Main.getIns().getMachineManager().getMachineAtLocation(e.getBlockAgainst().getLocation());

            if (machineAtLocation != null) {
                if (machineAtLocation.getConfiguration().isEquivalent(configuration)) {
                    e.setCancelled(true);

                    if (itemInHand.getAmount() > 1) {

                        itemInHand.setAmount(itemInHand.getAmount() - 1);

                    } else {

                        itemInHand = new ItemStack(Material.AIR);

                    }

                    e.getPlayer().setItemInHand(itemInHand);

                    machineAtLocation.incrementAmount();

                    return;
                }
            }

            Machine machineFromItem = Main.getIns().getMachineManager().getMachineFromItem(e.getPlayer(), e.getBlock(), e.getItemInHand());

            Main.getIns().getMachineManager().registerMachine(machineFromItem);

            MainData.getIns().getMessageManager().getMessage("PLACED_MACHINE").sendTo(e.getPlayer());
        }

    }

}
