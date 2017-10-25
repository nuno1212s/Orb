package com.nuno1212s.spawners.entitybundle;

import com.nuno1212s.spawners.main.Main;
import com.nuno1212s.util.LLocation;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EntityBundle {

    @Getter
    private UUID entity;

    @Getter
    private EntityType type;

    @Getter
    private LLocation spawnLocation;

    /**
     * Use WeakReference to the entity to prevent any memory leaks
     */
    private WeakReference<Entity> entityReference;

    @Getter
    private int mobCount;

    public EntityBundle(EntityType type, LLocation lastKnownLocation, int mobCount) {
        if (lastKnownLocation == null) {
            return;
        }

        this.type = type;
        this.mobCount = mobCount;

        Location location = lastKnownLocation.getLocation();
        //If the LLocation .getLocation returns null, the world is not loaded
        if (location == null) {
            this.entity = UUID.randomUUID();
            this.spawnLocation = lastKnownLocation;
            return;
        }

        System.out.println(location.getChunk().isLoaded());

        if (location.getChunk().isLoaded()) {
            load();
        } else {
            this.entity = UUID.randomUUID();
            this.spawnLocation = lastKnownLocation;
        }

        updateName();
    }

    public EntityBundle(EntityType type, Location spawnLocation) {
        Entity entity = spawnLocation.getWorld().spawnEntity(spawnLocation, type);
        this.entity = entity.getUniqueId();
        this.type = type;
        this.mobCount = 0;

        this.entityReference = new WeakReference<Entity>(entity);
    }

    public void addToBundle(int entityCount) {
        this.mobCount += entityCount;

        updateName();
    }

    public Entity getEntityReference() {
        return this.entityReference.get();
    }

    /**
     * Check if the entity is loaded
     * @return
     */
    public boolean isLoaded() {
        return this.entityReference == null || this.entityReference.get() == null;
    }

    /**
     * Kill the entity and drop all of the items
     */
    public void kill() {
        Entity entityReference = getEntityReference();
        EntityType type = entityReference.getType();

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

        Location location = entityReference.getLocation();

        for (ItemStack multipliedDrop : multipliedDrops) {
            location.getWorld().dropItemNaturally(location, multipliedDrop);
        }

        entityReference.remove();
    }

    public void updateName() {
        Entity entityReference = getEntityReference();
        entityReference.setCustomName(ChatColor.RED + "x" + String.valueOf(this.getMobCount()));
        entityReference.setCustomNameVisible(true);
    }

    /**
     * Remove the entity
     */
    public void remove() {
        getEntityReference().remove();
    }

    /**
     * First load of the entity, for when the chunk the entity is in is not loaded when the server starts up
     */
    public void load() {
        Location spawnLocation = getSpawnLocation().getLocation();
        Entity e = spawnLocation.getWorld().spawnEntity(spawnLocation, getType());

        this.entityReference = new WeakReference<Entity>(e);
        this.entity = e.getUniqueId();
        //When we load the actual entity, we do not need to remember the spawnLocation
        this.spawnLocation = null;

        updateName();
    }

    /**
     * Handle unloading the world the entity is contained in
     */
    public void unload() {
        if (!isLoaded()) return;

        this.spawnLocation = new LLocation(getEntityReference().getLocation());

        remove();

        this.entityReference = null;
        this.entity = UUID.randomUUID();
    }

}
