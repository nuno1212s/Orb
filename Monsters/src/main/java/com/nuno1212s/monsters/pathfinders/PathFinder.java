package com.nuno1212s.monsters.pathfinders;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.PathfinderGoal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public abstract class PathFinder extends PathfinderGoal {

    @Getter
    private final int id;

    @Getter
    @Setter
    LivingEntity entity;


    public PathFinder(int id) {
        this.id = id;
    }

    @Override
    public boolean a() {
        return tick(entity);
    }

    public abstract boolean tick(Entity e);

}
