package com.nuno1212s.crates.animations;

import com.nuno1212s.crates.crates.Crate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Animation class
 */
@AllArgsConstructor
public abstract class Animation {

    @Getter
    protected Inventory toEdit;

    @Getter
    protected Crate crate;

    @Getter
    protected boolean finished = false;

    @Getter
    protected Player player;

    /**
     * Run an animation cycle
     * @return Is the animation over. (true if it is and should stop being run, false if otherwise)
     */
    public abstract boolean run();

}
