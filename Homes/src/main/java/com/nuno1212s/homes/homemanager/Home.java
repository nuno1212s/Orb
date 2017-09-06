package com.nuno1212s.homes.homemanager;

import com.nuno1212s.homes.main.Main;
import com.nuno1212s.main.MainData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
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

    public void teleport(Player p) {

        if (Main.getIns().getHomeManager().getTimer().isTeleporting(p.getUniqueId())) {
            Main.getIns().getHomeManager().getTimer().cancelTeleport(p.getUniqueId());
            MainData.getIns().getMessageManager().getMessage("HOME_CANCELLED_ANOTHER_HOME").sendTo(p);
        }

        if (p.hasPermission("home.instantteleport")) {
            p.teleport(getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            MainData.getIns().getMessageManager().getMessage("TELEPORTED_HOME").sendTo(p);
        } else {
            Main.getIns().getHomeManager().getTimer().registerTeleport(p.getUniqueId(), this);
            MainData.getIns().getMessageManager().getMessage("TELEPORTING_IN")
                    .format("%time%", String.valueOf(Main.getIns().getHomeManager().getTimeNeeded())).sendTo(p);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Home) && ((Home) obj).getHomeName().equalsIgnoreCase(this.getHomeName());
    }
}
