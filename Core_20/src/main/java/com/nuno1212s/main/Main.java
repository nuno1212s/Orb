package com.nuno1212s.main;

import com.nuno1212s.events.PlayerDisconnectListener;
import com.nuno1212s.events.PlayerJoinListener;
import com.nuno1212s.modulemanager.ModuleManager;
import com.nuno1212s.mysql.MySql;
import com.nuno1212s.permissionmanager.PermissionManager;
import com.nuno1212s.playermanager.PlayerManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class
 */
public class Main extends JavaPlugin {

    @Getter
    private static Main ins;

    @Getter
    private ModuleManager moduleManager;

    @Getter
    private PermissionManager permissionManager;

    @Getter
    private PlayerManager playerManager;

    @Getter
    private MySql mySql;

    @Override
    public void onEnable() {
        ins = this;

        mySql = new MySql(this);
        moduleManager = new ModuleManager(this);
        permissionManager = new PermissionManager();
        playerManager = new PlayerManager();

        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDisconnectListener(), this);

    }

    @Override
    public void onDisable() {
        moduleManager.disable();
        mySql.closeConnection();
    }
}
