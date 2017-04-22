package com.nuno1212s.fullpvp.crates.animations;

import com.nuno1212s.fullpvp.crates.Crate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.Inventory;

/**
 * Animation class
 */
@AllArgsConstructor
public abstract class Animation {

    @Getter
    Inventory toEdit;

    @Getter
    Crate crate;

    /**
     * Run an animation cycle
     * @return Is the animation over. (true if it is and should stop being run, false if otherwise)
     */
    public abstract boolean run();

}
