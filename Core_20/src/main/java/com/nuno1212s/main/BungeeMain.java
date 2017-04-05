package com.nuno1212s.main;

import com.nuno1212s.config.BungeeConfig;
import com.nuno1212s.modulemanager.ModuleManager;
import com.nuno1212s.mysql.MySql;
import com.nuno1212s.permissionmanager.PermissionManager;
import com.nuno1212s.playermanager.PlayerManager;
import com.nuno1212s.scheduler.BungeeScheduler;
import com.nuno1212s.serverstatus.ServerManager;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;

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
        main.setDataFolder(this.getDataFolder());
        main.setMySql(
                new MySql(
                        new BungeeConfig(this,
                                new File(
                                        this.getDataFolder(), "config.yml"))));
        main.setServerManager(new ServerManager(this.getDataFolder()));
        main.setPermissionManager(new PermissionManager(false));
        main.setPlayerManager(new PlayerManager());
        main.setModuleManager(new ModuleManager(this.getDataFolder()));
        main.setScheduler(new BungeeScheduler(this.getProxy().getScheduler(), this));
    }

    @Override
    public void onDisable() {
        MainData ins = MainData.getIns();
        ins.getServerManager().save();
        ins.getModuleManager().disable();
        ins.getMySql().closeConnection();
    }
}
