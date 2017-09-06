package com.nuno1212s.homes.homemanager;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.json.simple.JSONObject;

/**
 * Home data class
 */
@Getter
public class Home {

    private String homeName;

    private String world;

    private double x, y, z;

    private float pitch, yaw;

    public Home(String homeName, Location l) {
        this.homeName = homeName;
        this.world = l.getWorld().getName();
        this.x = l.getX();
        this.y = l.getY();
        this.z = l.getZ();
        this.pitch = l.getPitch();
        this.yaw = l.getYaw();
    }

    public Home(String homeName, JSONObject object) {
        this.homeName = homeName;
        this.world = (String) object.get("World");
        this.x = (Double) object.get("X");
        this.y = (Double) object.get("Y");
        this.z = (Double) object.get("Z");
        this.pitch = ((Double) object.get("Pitch")).floatValue();
        this.yaw = ((Double) object.get("Yaw")).floatValue();
    }

    public void save(JSONObject dataObject) {
        JSONObject home = new JSONObject();
        home.put("World", world);
        home.put("X", x);
        home.put("Y", y);
        home.put("Z", z);
        home.put("Pitch", pitch);
        home.put("Yaw", yaw);
        dataObject.put(homeName, home);
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

}
