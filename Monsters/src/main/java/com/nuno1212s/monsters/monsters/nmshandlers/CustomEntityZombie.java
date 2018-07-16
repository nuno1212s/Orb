package com.nuno1212s.monsters.monsters.nmshandlers;

import com.nuno1212s.monsters.monsters.CustomEntity;
import com.nuno1212s.monsters.pathfinders.PathFinder;
import net.minecraft.server.v1_8_R3.EntityZombie;
import net.minecraft.server.v1_8_R3.World;

import java.util.List;

public class CustomEntityZombie extends EntityZombie {

    public CustomEntityZombie(World world, List<PathFinder> goalSelectors, List<PathFinder> targetSelectors) {
        super(world);

        CustomEntity.clearPathFinders(this);

        for (PathFinder goalSelector : goalSelectors) {
            this.goalSelector.a(goalSelector);
        }

        for (PathFinder targetSelector : targetSelectors) {
            this.targetSelector.a(targetSelector);
        }

    }

}
