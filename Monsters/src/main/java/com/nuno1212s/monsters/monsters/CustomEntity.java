package com.nuno1212s.monsters.monsters;

import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;

import java.lang.reflect.Field;

public class CustomEntity {

    public static void clearPathFinders(EntityInsentient e) {
        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            bField.setAccessible(true);
            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            bField.set(e.goalSelector, new UnsafeList<PathfinderGoalSelector>());
            bField.set(e.targetSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(e.goalSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(e.targetSelector, new UnsafeList<PathfinderGoalSelector>());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

}
