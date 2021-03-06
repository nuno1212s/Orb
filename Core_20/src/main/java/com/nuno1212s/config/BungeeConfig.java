package com.nuno1212s.config;

import com.nuno1212s.main.BungeeMain;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;

/**
 * Bungee Config
 */
public class BungeeConfig extends Config {

    Configuration c;

    public BungeeConfig(InputStream stream) {

        c = ConfigurationProvider.getProvider(YamlConfiguration.class).load(stream);

    }

    public BungeeConfig(Configuration c) {
        this.c = c;
    }

    public BungeeConfig(Plugin p, File config) {

        if (!config.exists()) {
            try {
                config.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            BungeeMain.getIns().saveResource(p, config, config.getName());
        }

        try {
            c = ConfigurationProvider.getProvider(YamlConfiguration.class).load(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object get(String key) {
        return c.get(key);
    }

    @Override
    public String getString(String key) {
        return c.getString(key, "");
    }

    @Override
    public int getInt(String key) {
        return c.getInt(key, 0);
    }

    @Override
    public double getDouble(String key) {
        return c.getDouble(key, 0D);
    }

    @Override
    public boolean getBoolean(String key) {
        return c.getBoolean(key, false);
    }

    @Override
    public void set(String key, Object value) {
        c.set(key, value);
    }

    @Override
    public Config getConfigurationSection(String key) {
        return new BungeeConfig(c.getSection(key));
    }

}
