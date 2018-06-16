package com.nuno1212s.minas.minemanager;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.FaweQueue;
import com.nuno1212s.main.MainData;
import com.nuno1212s.util.SerializableLocation;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Mina
 */
public class Mine {

    @Getter
    @Setter
    private String mineID, displayName;

    @Getter
    @Setter
    private long resetTimeMillis, lastReset;

    @Getter
    private Map<Integer, Material> mineMaterials;

    @Getter
    @Setter
    private Location corner1, corner2, defaultTP;

    public Mine(String mineID, String displayName, long resetTimeMillis, Map<Integer, Material> mineMaterials, Location corner1, Location corner2, Location defaultTP) {
        this.mineID = mineID;
        this.displayName = displayName;
        this.resetTimeMillis = resetTimeMillis;
        this.mineMaterials = mineMaterials;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.defaultTP = defaultTP;
    }

    public Mine(JSONObject data) {
        this.mineID = (String) data.get("MineID");
        this.displayName = (String) data.get("DisplayName");
        this.resetTimeMillis = (Long) data.get("ResetTime");
        this.mineMaterials = new HashMap<>();
        Map<String, Object> mineMaterials = (Map<String, Object>) data.get("MineMaterials");
        for (Map.Entry<String, Object> stringObjectEntry : mineMaterials.entrySet()) {
            this.mineMaterials.put(Integer.parseInt(stringObjectEntry.getKey()), Material.getMaterial((String) stringObjectEntry.getValue()));
        }
        this.corner1 = new SerializableLocation((JSONObject) data.get("Corner1"));
        this.corner2 = new SerializableLocation((JSONObject) data.get("Corner2"));
        this.defaultTP = new SerializableLocation((JSONObject) data.get("DefaultTP"));
    }

    public void calculateLocations() {

        Location l1 = corner1.clone();
        Location l2 = corner2.clone();

        double x1 = Double.min(l1.getX(), l2.getX()), x2 = Double.max(l1.getX(), l2.getX());
        double y1 = Double.min(l1.getY(), l2.getY()), y2 = Double.max(l1.getY(), l2.getY());
        double z1 = Double.min(l1.getZ(), l2.getZ()), z2 = Double.max(l1.getZ(), l2.getZ());

        corner1 = new Location(l1.getWorld(), x1, y1, z1);
        corner2 = new Location(l1.getWorld(), x2, y2, z2);

    }

    public boolean shouldReset() {
        return corner1 != null && corner2 != null && this.lastReset + this.resetTimeMillis <= System.currentTimeMillis();
    }

    public boolean isInMine(Location l) {
        if (corner1 == null || corner2 == null) {
            return false;
        }

        if (!corner1.getWorld().getName().equalsIgnoreCase(l.getWorld().getName())) {
            return false;
        }

        return corner1.getX() <= l.getX() && corner2.getX() >= l.getX()
                && corner1.getY() <= l.getY() && corner2.getY() >= l.getY()
                && corner1.getZ() <= l.getZ() && corner2.getZ() >= l.getZ();

    }

    public void addMaterial(int prob, Material mat) {
        this.mineMaterials.put(prob, mat);
    }

    public void resetMine() {
        lastReset = System.currentTimeMillis();
        World world = corner1.getWorld();

        FaweQueue queue = FaweAPI.
                createQueue(FaweAPI.getWorld(world.getName()), false);

        for (Player p : world.getPlayers()) {
            if (isInMine(p.getLocation())) {
                p.teleport(this.defaultTP);
            }
        }

        //Bukkit.getServer().broadcastMessage(MainData.getIns().getMessageManager().getMessage("RESET_MINE").format("%mina%", getDisplayName()).toString());

        MainData.getIns().getScheduler().runTaskAsync(() -> {
            for (int x = corner1.getBlockX(); x <= corner2.getBlockX(); x++) {
                for (int y = corner1.getBlockY(); y <= corner2.getBlockY(); y++) {
                    for (int z = corner1.getBlockZ(); z <= corner2.getBlockZ(); z++) {
                        queue.setBlock(x, y, z, getRandomMaterial().getId());
                    }
                }
            }
            queue.flush();
        });
    }

    private Material getRandomMaterial() {
        int maxProb = 0;
        for (Integer integer : mineMaterials.keySet()) {
            maxProb += integer;
        }

        int prob = new Random().nextInt(maxProb), currentProb = 0;

        for (Map.Entry<Integer, Material> integerMaterialEntry : mineMaterials.entrySet()) {
            if (currentProb <= prob && (currentProb += integerMaterialEntry.getKey()) >= prob) {
                return integerMaterialEntry.getValue();
            }
        }

        return null;
    }

    public void save(JSONObject jsonObject) {
        jsonObject.put("MineID", this.mineID);
        jsonObject.put("DisplayName", this.displayName);
        jsonObject.put("ResetTime", this.resetTimeMillis);
        JSONObject jO = new JSONObject();
        this.mineMaterials.forEach((integer, material) -> {
            jO.put(String.valueOf(integer), material.name());
        });
        jsonObject.put("MineMaterials", jO);
        SerializableLocation corner1l = new SerializableLocation(this.corner1),
                corner2l = new SerializableLocation(this.corner2)
                , defaultTP = new SerializableLocation(this.defaultTP);
        JSONObject corner1 = new JSONObject(), corner2 = new JSONObject(), defaultTPl = new JSONObject();
        corner1l.save(corner1);
        corner2l.save(corner2);
        defaultTP.save(defaultTPl);
        jsonObject.put("Corner1", corner1);
        jsonObject.put("Corner2", corner2);
        jsonObject.put("DefaultTP", defaultTPl);

    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Mine && ((Mine) obj).getMineID().equalsIgnoreCase(mineID);
    }
}
