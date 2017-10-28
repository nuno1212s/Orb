package com.nuno1212s.spawners.listeners;

import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Map;

public class SpawnerPlaceListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        NBTCompound nbt = new NBTCompound(e.getItemInHand());

        Map<String, Object> values = nbt.getValues();
        if (!values.containsKey("MobType")) {
            return;
        }

        String mobType = (String) values.get("MobType");
        EntityType type = EntityType.valueOf(mobType);

        if (e.getBlock().getState() instanceof CreatureSpawner) {
            //Reduce the delay of the mob spawning
            ((CreatureSpawner) e.getBlock().getState()).setSpawnedType(type);

            ((CreatureSpawner) e.getBlock().getState()).setDelay(1);
            e.getBlock().getState().update();
        }
    }

}
