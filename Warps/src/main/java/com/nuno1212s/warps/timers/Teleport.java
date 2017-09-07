package com.nuno1212s.warps.timers;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Teleport class interfacce
 */
public interface Teleport {

    long getTimeNeeded();

    Location getLocation();

}
