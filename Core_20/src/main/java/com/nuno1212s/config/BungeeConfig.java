package com.nuno1212s.config;

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

            saveResource(p, config, config.getName());
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

    public void saveResource(Plugin p, File path, String resource) {
        if (!path.exists()) {
            try {
                path.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        InputStream resourceAsStream = p.getResourceAsStream(resource);
        OutputStream o = null;

        try {
            o = new FileOutputStream(path);

            byte[] bytes = new byte[1024];

            int length;

            while ((length = resourceAsStream.read(bytes)) != -1) {
                o.write(bytes, 0, length);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resourceAsStream != null) {
                    resourceAsStream.close();
                }
                if (o != null) {
                    o.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
