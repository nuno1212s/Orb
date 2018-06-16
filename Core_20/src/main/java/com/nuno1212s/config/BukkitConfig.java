package com.nuno1212s.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.reader.StreamReader;

import java.io.*;

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
        try (Reader r = new InputStreamReader(stream)) {
            this.config = YamlConfiguration.loadConfiguration(r);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        ConfigurationSection configurationSection = config.getConfigurationSection(key);
        if (configurationSection == null) {
            return null;
        }
        return new BukkitConfig(configurationSection);
    }
}
