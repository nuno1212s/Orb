package com.nuno1212s.main;

import com.nuno1212s.config.BungeeConfig;
import com.nuno1212s.events.eventcaller.EventCaller;
import com.nuno1212s.messagemanager.Messages;
import com.nuno1212s.modulemanager.ModuleManager;
import com.nuno1212s.mysql.MySql;
import com.nuno1212s.permissionmanager.PermissionManager;
import com.nuno1212s.playermanager.PlayerManager;
import com.nuno1212s.rediscommunication.RedisHandler;
import com.nuno1212s.rewards.bungee.BungeeRewardManager;
import com.nuno1212s.scheduler.BungeeScheduler;
import com.nuno1212s.server_sender.BungeeSender;
import com.nuno1212s.serverstatus.ServerManager;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.*;

/**
 * Main bungee class
 */
public class BungeeMain extends Plugin {

    @Getter
    static BungeeMain ins;

    @Override
    public void onEnable() {
        ins = this;
        MainData main = new MainData();
        main.setBungee(true);
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdirs();
        }
        main.setEventCaller((o) -> {});
        main.setDataFolder(this.getDataFolder());
        File config = new File(this.getDataFolder(), "config.yml"),
                messages = new File(this.getDataFolder(), "messages.json");
        main.setScheduler(new BungeeScheduler(this.getProxy().getScheduler(), this));
        BungeeConfig bungeeConfig = new BungeeConfig(this, config);
        main.setMySql(new MySql(bungeeConfig));
        main.setMessageManager(new Messages(messages));
        main.setRedisHandler(new RedisHandler(bungeeConfig));
        main.setServerManager(new ServerManager(this.getDataFolder()));
        main.setPermissionManager(new PermissionManager(false));
        main.setPlayerManager(new PlayerManager());
        main.setRewardManager(new BungeeRewardManager());
        main.setModuleManager(new ModuleManager(this.getDataFolder(), getClass().getClassLoader()));
        main.getMessageManager().reloadMessages();

        new BungeeSender(this);
    }

    @Override
    public void onDisable() {
        MainData ins = MainData.getIns();
        ins.getServerManager().save();
        ins.getModuleManager().disable();
        ins.getMySql().closeConnection();
        ins.getRedisHandler().close();
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
