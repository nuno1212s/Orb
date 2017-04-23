package com.nuno1212s.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Serializable location
 */
public class SerializableLocation extends Location {

    public SerializableLocation(Location l) {
        super(l.getWorld(), l.getX(), l.getY(), l.getZ());
    }

    public SerializableLocation(String location) {
        super(Bukkit.getWorlds().get(0), 0, 0, 0);
        String[] split = location.split(",");
        String world = split[0];
        this.setWorld(Bukkit.getWorld(world));
        this.setX(Integer.parseInt(split[1]));
        this.setY(Integer.parseInt(split[2]));
        this.setZ(Integer.parseInt(split[3]));
    }

    public SerializableLocation(ConfigurationSection cs) {
        super(Bukkit.getWorld(cs.getString("World", "world")), cs.getDouble("X"), cs.getDouble("Y"), cs.getDouble("Z"));
    }
    
    public String toString() {
        int blockX = this.getBlockX();
        int blockY = this.getBlockY();
        int blockZ = this.getBlockZ();
        return this.getWorld().getName() + "," + String.valueOf(blockX) + "," + String.valueOf(blockY) + "," + String.valueOf(blockZ);
    }

    public void save(ConfigurationSection cs) {
        cs.set("World", this.getWorld().getName());
        cs.set("X", this.getX());
        cs.set("Y", this.getY());
        cs.set("Z", this.getZ());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Location) {
            Location location = (Location) obj;
            return ((Location) obj).getWorld().getName().equalsIgnoreCase(this.getWorld().getName()) && location.getBlockX() == this.getBlockX() && location.getBlockY() == this.getBlockY() && location.getBlockZ() == this.getBlockZ();
        }
        return false;
    }
}
