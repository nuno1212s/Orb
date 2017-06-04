package com.nuno1212s.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;

/**
 * Handles bukkit config
 */
public class BukkitConfig extends Config {

    private ConfigurationSection config;

    public BukkitConfig(ConfigurationSection config) {
        this.config = config;
    }

    public BukkitConfig(File f) {
        this.config = YamlConfiguration.loadConfiguration(f);
    }

    public BukkitConfig(InputStream stream) {
        this.config = YamlConfiguration.loadConfiguration(stream);
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
    public boolean getBoolean(String key) {
        return config.getBoolean(key);
    }

    @Override
    public void set(String key, Object value) {
        config.set(key, value);
    }

    @Override
    public Config getConfigurationSection(String key) {
        return new BukkitConfig(config.getConfigurationSection(key));
    }
}
