package com.nuno1212s.hub.utils;

import com.nuno1212s.hub.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class SpawnManager {

    private Main m;
    private static SpawnManager ins;

    public static SpawnManager getIns() {return ins;}

    public SpawnManager(Main m) {
        this.m = m;
        this.ins = this;
        if (!m.getConfig().contains("SpawnLocation")) {
            ConfigUtils.getIns().setLocation("SpawnLocation", Bukkit.getWorlds().get(0).getSpawnLocation(), m.getConfig());
            m.saveConfig();
        }
        this.spawnLocation = ConfigUtils.getIns().getLocation("SpawnLocation", m.getConfig());
    }

    public void setSpawnLocation(Location l) {
        this.spawnLocation = l;
        ConfigUtils.getIns().setLocation("SpawnLocation", Bukkit.getWorlds().get(0).getSpawnLocation(), m.getConfig());
        m.saveConfig();
    }

    public Location spawnLocation;

}
