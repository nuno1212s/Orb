package com.nuno1212s.machines.listeners;

import com.nuno1212s.machines.machinemanager.Machine;
import com.nuno1212s.machines.machinemanager.MachineConfiguration;
import com.nuno1212s.machines.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.Pair;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class MachinePlaceListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {

        ItemStack itemInHand = e.getItemInHand();

        Pair<MachineConfiguration, Integer> configuration = MachineConfiguration.fromItemWithAmount(itemInHand);
        if (configuration.getKey() != null) {

            Machine machineAtLocation = Main.getIns().getMachineManager().getMachineAtLocation(e.getBlockAgainst().getLocation());

            if (machineAtLocation != null) {
                if (machineAtLocation.getConfiguration().isEquivalent(configuration.getKey())
                        && machineAtLocation.getOwner().equals(e.getPlayer().getUniqueId())) {
                    e.setCancelled(true);

                    if (itemInHand.getAmount() > 1) {

                        itemInHand.setAmount(itemInHand.getAmount() - 1);

                    } else {

                        itemInHand = new ItemStack(Material.AIR);

                    }

                    e.getPlayer().setItemInHand(itemInHand);

                    machineAtLocation.incrementAmount(configuration.getValue());

                    return;
                }
            }

            Machine machineFromItem = Main.getIns().getMachineManager().getMachineFromItem(e.getPlayer(), e.getBlock(), e.getItemInHand());

            if (machineFromItem == null) {
                return;
            }

            Main.getIns().getMachineManager().registerMachine(machineFromItem);

            MainData.getIns().getMessageManager().getMessage("PLACED_MACHINE")
                    .format("%name%", configuration.getKey().getName()).sendTo(e.getPlayer());
        }

    }

}
