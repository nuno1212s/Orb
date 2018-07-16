package com.nuno1212s.monsters.pathfinders.defaultPathFinders;

import com.nuno1212s.monsters.pathfinders.PathFinder;
import org.bukkit.entity.Entity;

public class ClosestInRange extends PathFinder {

    public ClosestInRange() {
        super(1);
    }

    @Override
    public boolean tick(Entity e) {
        return false;
    }
}
