package com.nuno1212s.monsters.pathfinders;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.PathfinderGoalTarget;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTargetEvent;

public abstract class TargetFinder extends PathfinderGoalTarget {

    @Getter
    private Entity target;

    public TargetFinder(Creature entityCreature, boolean b) {
        super(((CraftCreature) entityCreature).getHandle(), b);
    }

    @Override
    public boolean a() {
        return shouldStart();
    }

    public abstract boolean shouldStart();

    public abstract void execute();

    protected Entity getEntity() {
        return this.e.getBukkitEntity();
    }

    /**
     * Sets the target of the mob this pathfinder is controlling
     * @param c
     */
    protected void setTarget(Creature c) {
        this.target = c;

        this.e.setGoalTarget(((CraftLivingEntity) c).getHandle(), EntityTargetEvent.TargetReason.UNKNOWN, true);
    }
}
