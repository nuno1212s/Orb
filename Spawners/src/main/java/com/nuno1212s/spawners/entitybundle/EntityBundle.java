package com.nuno1212s.spawners.entitybundle;

import com.nuno1212s.spawners.main.Main;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EntityBundle {

    @Getter
    private Entity entity;

    @Getter
    private int mobCount;

    public EntityBundle(EntityType type, Location lastKnownLocation, int mobCount) {
        if (lastKnownLocation == null) {
            return;
        }

        this.mobCount = mobCount;

        this.entity = lastKnownLocation.getWorld().spawnEntity(lastKnownLocation, type);

        // TODO: 25-10-2017 update name
    }

    public EntityBundle(Entity toBundle) {
        this.entity = toBundle;
        this.mobCount = 1;
    }

    public void addToBundle(int entityCount) {
        this.mobCount += entityCount;

        // TODO: 25-10-2017 Update entity display name to fit the new mob count
    }

    /**
     * Kill the entity and drop all of the items
     */
    public void kill() {
        EntityType type = getEntity().getType();

        ItemStack[] dropsForEntity = Main.getIns().getEntityManager().getDropsForEntity(type);

        List<ItemStack> multipliedDrops = new ArrayList<>();

        for (ItemStack item : dropsForEntity) {
            int itemAmount = item.getAmount() * mobCount;
            int stacks;
            if (itemAmount % item.getMaxStackSize() == 0) {
                stacks = itemAmount / item.getMaxStackSize();

                for (int i = 0; i < stacks; i++) {
                    ItemStack clone = item.clone();
                    clone.setAmount(item.getMaxStackSize());
                    multipliedDrops.add(clone);
                }
            } else {
                stacks = itemAmount / item.getMaxStackSize() + 1;

                for (int i = 0; i < stacks; i++) {
                    ItemStack clone = item.clone();
                    clone.setAmount((itemAmount >= item.getMaxStackSize()) ? item.getMaxStackSize() : itemAmount);
                    itemAmount -= item.getMaxStackSize();
                }
            }

        }

        Location location = getEntity().getLocation();

        for (ItemStack multipliedDrop : multipliedDrops) {
            location.getWorld().dropItemNaturally(location, multipliedDrop);
        }

        getEntity().remove();
    }

    /**
     * Remove the entity
     */
    public void remove() {
        getEntity().remove();
    }

}
