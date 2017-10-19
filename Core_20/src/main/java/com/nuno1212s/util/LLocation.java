package com.nuno1212s.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
@AllArgsConstructor
public class LLocation {

    private double x, y, z;

    private float pitch, yaw;

    private String world;

    public LLocation(Location l) {
        this.x = l.getX();
        this.y = l.getY();
        this.z = l.getZ();
        this.pitch = l.getPitch();
        this.yaw = l.getYaw();
        this.world = l.getWorld().getName();
    }

    public Location getLocation() {
        World world = Bukkit.getWorld(this.world);

        if (world == null) {
            world = Bukkit.getWorlds().get(0);
        }

        return new Location(world, x, y, z, yaw, pitch);
    }

}
