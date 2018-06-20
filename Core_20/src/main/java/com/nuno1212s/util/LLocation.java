package com.nuno1212s.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.NumberConversions;

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

    public int getBlockX() {
        return NumberConversions.floor(this.x);
    }

    public int getBlockY() {
        return NumberConversions.floor(this.y);
    }

    public int getBlockZ() {
        return NumberConversions.floor(this.z);
    }

    public Location getLocation() {
        World world = Bukkit.getWorld(this.world);

        if (world == null) {
            world = Bukkit.getWorlds().get(0);
        }

        return new Location(world, x, y, z, yaw, pitch);
    }

    public boolean equals(Location l) {

        if (l.getWorld().getName().equalsIgnoreCase(this.getWorld())) {
            if (l.getBlockX() == getBlockX() && l.getBlockY() == getBlockY() && l.getBlockZ() == getBlockZ()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LLocation) {
            LLocation l = (LLocation) obj;

            if (l.getWorld().equalsIgnoreCase(getWorld())) {
                if (l.getBlockX() == getBlockX() && l.getBlockY() == getBlockY() && l.getBlockZ() == getBlockZ()) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }
}
