package com.nuno1212s.fullpvp.crates.animations.AnimationTimer;

import com.nuno1212s.fullpvp.crates.animations.Animation;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles timers
 */
public class AnimationTimer implements Runnable {

    private final List<Animation> animations = Collections.synchronizedList(new ArrayList<>());

    public void registerAnimation(Animation anim) {
        animations.add(anim);
    }

    public boolean isInventoryRegistered(Inventory i) {
        synchronized (animations) {
            for (Animation a : this.animations) {
                if (a.getToEdit().getTitle().equalsIgnoreCase(i.getTitle())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Animation getAnimation(Inventory i) {
        synchronized (animations) {
            for (Animation animation : animations) {
                if (animation.getToEdit().getTitle().equalsIgnoreCase(i.getTitle())) {
                    return animation;
                }
            }
        }
        return null;
    }

    public boolean cancelAnimation(Inventory i) {
        synchronized (animations) {
            return animations.removeIf(animation -> {
                return animation.getToEdit().getTitle().equalsIgnoreCase(i.getTitle());
            });
        }
    }

    public boolean cancelAnimation(Animation anim) {
        return animations.remove(anim);
    }

    @Override
    public void run() {
        synchronized (animations) {
            animations.removeIf(Animation::run);
        }
    }
}
