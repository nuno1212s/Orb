package com.nuno1212s.monsters.monsters.nmshandlers;

import com.nuno1212s.monsters.monsters.CustomEntity;
import com.nuno1212s.monsters.monsters.CustomizableEntity;
import net.minecraft.server.v1_8_R3.EntityZombie;
import net.minecraft.server.v1_8_R3.PathfinderGoal;
import net.minecraft.server.v1_8_R3.World;

import java.util.List;

public class CustomEntityZombie extends EntityZombie implements CustomizableEntity {

    public CustomEntityZombie(World world) {
        super(world);

        CustomEntity.clearPathFinders(this);
    }

    @Override
    public void setPathFinders(List<? extends PathfinderGoal> pathFinders) {
        
    }

    @Override
    public void setTargetSelectors(List<? extends PathfinderGoal> targetFinders) {

    }
}
