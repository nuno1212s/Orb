package com.nuno1212s.main;

import com.nuno1212s.config.BukkitConfig;
import com.nuno1212s.events.PlayerDisconnectListener;
import com.nuno1212s.events.PlayerJoinListener;
import com.nuno1212s.modulemanager.ModuleManager;
import com.nuno1212s.mysql.MySql;
import com.nuno1212s.permissionmanager.PermissionManager;
import com.nuno1212s.playermanager.PlayerManager;
import com.nuno1212s.scheduler.BukkitScheduler;
import com.nuno1212s.serverstatus.ServerManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class
 */
public class Main extends JavaPlugin {

    @Getter
    static Main ins;

    @Override
    public void onEnable() {
        ins = this;
        MainData data = new MainData();

        this.saveDefaultConfig();

        data.setMySql(new MySql(new BukkitConfig(this.getConfig())));
        data.setServerManager(new ServerManager(this.getDataFolder()));
        data.setPermissionManager(new PermissionManager(true));
        data.setPlayerManager(new PlayerManager());
        data.setModuleManager(new ModuleManager(this.getDataFolder()));
        data.setScheduler(new BukkitScheduler(this.getServer().getScheduler(), this));

        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDisconnectListener(), this);

    }

    @Override
    public void onDisable() {
        /*serverManager.save();
        moduleManager.disable();
        mySql.closeConnection();*/
    }

}
