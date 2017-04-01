package com.nuno1212s.core.main;

import com.nuno1212s.core.commandmanager.*;
import com.nuno1212s.core.events.PlayerCommandEvent;
import com.nuno1212s.core.events.PlayerLoginEvent;
import com.nuno1212s.core.events.PlayerMoveListener;
import com.nuno1212s.core.messagemanager.Messages;
import com.nuno1212s.core.modulemanager.ModuleManager;
import com.nuno1212s.core.permissions.PermissionsListeners;
import com.nuno1212s.core.serverstatus.ServerStatus;
import com.nuno1212s.core.serverstatus.SetCanSave;
import com.nuno1212s.core.configmanager.MainConfig;
import com.nuno1212s.core.confirm.ConfirmationManager;
import com.nuno1212s.core.events.PlayerDisconnectEvent;
import com.nuno1212s.core.mysql.MySqlDB;
import com.nuno1212s.core.playermanager.PlayerManager;
import com.nuno1212s.core.servertick.TickServer;
import com.nuno1212s.core.util.BungeeSender;
import com.nuno1212s.core.util.LoginEvent;
import com.nuno1212s.core.util.PermissionServer;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

/**
 * Main plugin class
 */
@Getter
public class Main extends JavaPlugin {

    @Getter
    private static Main ins;

    @Getter
    public PermissionServer serverPermissions;

    @Getter
    private String ServerName, ServerType;

    @Getter
    private LoginEvent event;

    @Getter
    private Messages messages;

    public void registerPermissions(PermissionServer s) {
        serverPermissions = s;
    }

    public void registerEvent(LoginEvent e) {
        event = e;
    }

    @Getter
    String version;

    public static Main getInstance() {
        return ins;
    }

    @Override
    public void onEnable() {
        ins = this;

        saveDefaultConfig();

        FileConfiguration fc = this.getConfig();
        this.ServerName = fc.getString("ServerName");
        this.ServerType = fc.getString("ServerType", "Hub");
        this.saveConfig();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "TELLINFO");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "GROUPUPDATE");

        new PlayerManager(this);
        new MySqlDB(this);
        new ConfirmationManager();
        new BungeeSender(this);
        new ModuleManager(this);
        messages = new Messages(this);
        //PermissionsGroupManager.getIns().load();
        MainConfig.getIns().load(this);

        getCommand("group").setExecutor(new group());
        getCommand("setcansave").setExecutor(new SetCanSave());
        getCommand("chat").setExecutor(new chat());
        getCommand("ping").setExecutor(new ping());
        getCommand("confirm").setExecutor(new ConfirmCommand());
        getCommand("deny").setExecutor(new DenyCommand());

        getServer().getScheduler().runTaskTimerAsynchronously(this, new TickServer(this), 0, 1200);

        getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerCommandEvent(), this);
        getServer().getPluginManager().registerEvents(new ServerStatus(), this);
        getServer().getPluginManager().registerEvents(new PlayerLoginEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerDisconnectEvent(), this);
        getServer().getPluginManager().registerEvents(new PermissionsListeners(), this);

        String name = Bukkit.getServer().getClass().getPackage().getName();
        String mcVersion = name.substring(name.lastIndexOf('.') + 1).replace("org.bukkit.craftbukkit.", "");
        version = mcVersion + ".";
    }

    @Override
    public void onDisable() {
        ServerStatus.getIns().handleShutdown();
        MySqlDB.getIns().closeConnection();
    }

}
