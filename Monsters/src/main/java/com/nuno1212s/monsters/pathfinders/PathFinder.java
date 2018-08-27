package com.nuno1212s.monsters.pathfinders;

import net.minecraft.server.v1_8_R3.PathfinderGoal;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PathFinder{

    public List<? extends PathfinderGoal> loadPathFinders(Object entity, JSONObject config) {

        for (Object o : config.keySet()) {

        }

        return new ArrayList<>();
    }

}
