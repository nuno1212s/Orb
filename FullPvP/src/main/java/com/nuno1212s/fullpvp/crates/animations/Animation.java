package com.nuno1212s.fullpvp.crates.animations;

import com.nuno1212s.fullpvp.crates.Reward;
import lombok.AllArgsConstructor;
import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * Animation class
 */
@AllArgsConstructor
public abstract class Animation {

    Inventory toEdit;

    List<Reward> possibleRewards;

    /**
     * Run an animation cycle
     * @return Is the animation over. (true if it is and should stop being run, false if otherwise)
     */
    public abstract boolean run();

}
