package com.nuno1212s.spawners.listeners;

import com.nuno1212s.main.MainData;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpawnerBreakListener implements Listener {

    //@EventHandler
    public void onSpawnerBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.MOB_SPAWNER) {
            CreatureSpawner state = (CreatureSpawner) e.getBlock().getState();

            ItemStack item = new ItemStack(Material.MOB_SPAWNER);

            ItemMeta itemMeta = item.getItemMeta();

            itemMeta.setDisplayName(MainData.getIns().getMessageManager().getMessage(state.getSpawnedType().name()).toString());

            item.setItemMeta(itemMeta);

            NBTCompound nbt = new NBTCompound(item);

            nbt.add("MobType", state.getSpawnedType().name());

            e.getPlayer().getInventory().addItem(nbt.write(item));

            e.setCancelled(true);
            e.getBlock().setType(Material.AIR);
        }
    }

}
