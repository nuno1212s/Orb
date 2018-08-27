package com.nuno1212s.monsters.monsters;


import net.minecraft.server.v1_8_R3.PathfinderGoal;

import java.util.List;

public interface CustomizableEntity {

    void setPathFinders(List<? extends PathfinderGoal> pathFinders);

    void setTargetSelectors(List<? extends PathfinderGoal> targetFinders);

}
