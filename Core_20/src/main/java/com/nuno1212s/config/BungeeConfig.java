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

    public BungeeConfig(Plugin p, File config) {

        if (!config.exists()) {
            try {
                config.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            saveResource(p, config, "config.yml");
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
        return c.getString(key);
    }

    @Override
    public int getInt(String key) {
        return c.getInt(key);
    }

    @Override
    public double getDouble(String key) {
        return c.getDouble(key);
    }

    @Override
    public void set(String key, Object value) {
        c.set(key, value);
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
