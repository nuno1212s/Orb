package com.nuno1212s.monsters.monsters.nmshandlers;

import com.nuno1212s.monsters.monsters.CustomEntity;
import com.nuno1212s.monsters.pathfinders.PathFinder;
import net.minecraft.server.v1_8_R3.EntityWolf;
import net.minecraft.server.v1_8_R3.World;

import java.util.List;

public class CustomEntityWolf extends EntityWolf {

    public CustomEntityWolf(World world, List<PathFinder> goalSelector, List<PathFinder> targetSelector) {
        super(world);

        CustomEntity.clearPathFinders(this);

        for (PathFinder pathFinder : goalSelector) {

            this.goalSelector.a(pathFinder);

        }

        for (PathFinder pathFinder : targetSelector) {

            this.targetSelector.a(pathFinder);

        }


    }
}
