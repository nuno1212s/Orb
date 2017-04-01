package com.nuno1212s.confighandler;

import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import com.nuno1212s.main.Main;

import java.io.*;

public class Config {

    static Config ins = new Config();

    public static Config getIns() {
        return ins;
    }

    @Getter
    public Configuration c;

    private File f;

    /**
     * Load configuration file
     *
     * @param m
     */
    public void loadFile(Main m) {
        if (!m.getDataFolder().exists()) {
            m.getDataFolder().mkdirs();
        }
        f = new File(m.getDataFolder(), "config.yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*
             * Write Defaults to file
             */
            saveResource(m, f, "config.yml");
        }

        /*
         * Load configuration class
         */
        try {
            c = ConfigurationProvider.getProvider(YamlConfiguration.class).load(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(c, f);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

            int length = 0;

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
