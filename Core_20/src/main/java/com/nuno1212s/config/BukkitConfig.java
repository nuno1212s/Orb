package com.nuno1212s.config;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Handles bukkit config
 */
public class BukkitConfig extends Config {

    private FileConfiguration config;

    public BukkitConfig(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public String getString(String key) {
        return config.getString(key);
    }

    @Override
    public double getDouble(String key) {
        return config.getDouble(key);
    }

    @Override
    public Object get(String key) {
        return config.get(key);
    }

    @Override
    public int getInt(String key) {
        return config.getInt(key);
    }

    @Override
    public void set(String key, Object value) {
        config.set(key, value);
    }
}
