package com.nuno1212s.monsters.monsters.nmshandlers;

import com.nuno1212s.monsters.monsters.CustomEntity;
import com.nuno1212s.monsters.pathfinders.PathFinder;
import net.minecraft.server.v1_8_R3.EntityWolf;
import net.minecraft.server.v1_8_R3.World;

import java.util.List;

public class CustomEntityWolf extends EntityWolf {

    public CustomEntityWolf(World world) {
        super(world);

        CustomEntity.clearPathFinders(this);


    }
}
