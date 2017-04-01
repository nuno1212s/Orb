package com.nuno1212s.main;

import com.google.common.cache.CacheBuilder;

import com.nuno1212s.commands.*;
import com.nuno1212s.confighandler.Config;
import com.nuno1212s.events.LoginEvent;
import com.nuno1212s.events.PermissionCheckEvent;
import com.nuno1212s.events.QuitEvent;
import com.nuno1212s.loginhandling.events.PluginMessageListener;
import com.nuno1212s.mysql.MySqlHandler;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import com.nuno1212s.events.PluginMessage;
import com.nuno1212s.loginhandling.MojangAPIConnector;
import com.nuno1212s.loginhandling.events.PlayerLoginEvent;
import com.nuno1212s.permissions.PermissionsGroupManager;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class Main extends Plugin{

    private MojangAPIConnector connector;
    
    @Getter private static Main instance;
    
    @Override
    public void onEnable() {
    	instance = this;

        Config.getIns().loadFile(this);

        new MySqlHandler(this);
        PermissionsGroupManager.getIns().load();

        getProxy().getPluginManager().registerCommand(this, new BuildCommand());
        getProxy().getPluginManager().registerCommand(this, new HubCommand());
        getProxy().getPluginManager().registerCommand(this, new SCommand());
        getProxy().getPluginManager().registerCommand(this, new StaffCommand());
        getProxy().getPluginManager().registerCommand(this, new RCommand());
        getProxy().getPluginManager().registerCommand(this, new TellCommand());

        getProxy().getPluginManager().registerListener(this, new PermissionCheckEvent());
        getProxy().getPluginManager().registerListener(this, new LoginEvent(this));
        getProxy().getPluginManager().registerListener(this, new PlayerLoginEvent(this));
        getProxy().getPluginManager().registerListener(this, new QuitEvent());
        getProxy().getPluginManager().registerListener(this, new PluginMessageListener(this));
        getProxy().getPluginManager().registerListener(this, new PluginMessage());

        ConcurrentMap<Object, Object> requestCache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES).build().asMap();

        List<String> stringList = Config.getIns().getC().getStringList("ip-addresses");

        connector = new MojangAPIConnector(requestCache, getLogger(), stringList, 600);

        System.out.println(stringList);

        getProxy().registerChannel("AUTOLOGIN");
        getProxy().registerChannel("TELLINFO");
        getProxy().registerChannel("GROUPUPDATE");

    }

    @Override
    public void onDisable() {
        MySqlHandler.getIns().closeConnection();
    }

    public MojangAPIConnector getConnector() {
        return connector;
    }
}
