package com.nuno1212s.bungee.main;

import com.google.common.cache.CacheBuilder;
import com.nuno1212s.bungee.commands.RCommand;
import com.nuno1212s.bungee.commands.StaffCommand;
import com.nuno1212s.bungee.commands.TellCommand;
import com.nuno1212s.bungee.events.*;
import com.nuno1212s.bungee.loginhandler.MojangAPIConnector;
import com.nuno1212s.bungee.loginhandler.events.PlayerLoginEvent;
import com.nuno1212s.bungee.loginhandler.events.PluginMessageListener;
import com.nuno1212s.bungee.motd.MOTDCommand;
import com.nuno1212s.bungee.motd.ServerMOTD;
import com.nuno1212s.config.BungeeConfig;
import com.nuno1212s.config.Config;
import com.nuno1212s.main.BungeeMain;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Main module class
 */
@ModuleData(name = "Bungee", version = "0.1", dependencies = {})
public class Main extends Module {

    @Getter
    static Main ins;

    @Getter
    static BungeeMain plugin;

    @Getter
    private MojangAPIConnector connector;

    @Getter
    private ServerMOTD motdManager;

    @Override
    public void onEnable() {
        ins = this;
        plugin = BungeeMain.getIns();

        Config c = new BungeeConfig(BungeeMain.getIns(), new File(this.getDataFolder(), "config.yml"));

        motdManager = new ServerMOTD(this.getDataFolder());
        getProxy().getPluginManager().registerListener(plugin, motdManager);
        getProxy().getPluginManager().registerListener(plugin, new LoginEvent());
        getProxy().getPluginManager().registerListener(plugin, new PlayerLoginEvent(this));
        getProxy().getPluginManager().registerListener(plugin, new PluginMessageListener());
        getProxy().getPluginManager().registerListener(plugin, new QuitEvent());
        getProxy().getPluginManager().registerListener(plugin, new PluginMessage());
        getProxy().getPluginManager().registerListener(plugin, new PermissionCheckListener());

        getProxy().getPluginManager().registerCommand(plugin, new MOTDCommand());
        getProxy().getPluginManager().registerCommand(plugin, new RCommand());
        getProxy().getPluginManager().registerCommand(plugin, new StaffCommand());
        getProxy().getPluginManager().registerCommand(plugin, new TellCommand());

        ConcurrentMap<Object, Object> requestCache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES).build().asMap();

        List<String> stringList = c.get("ip-addresses") == null ? new ArrayList<>() : (List<String>) c.get("ip-addresses");

        connector = new MojangAPIConnector(requestCache, getProxy().getLogger(), stringList, 600);

        System.out.println(stringList);

        getProxy().registerChannel("AUTOLOGIN");
        getProxy().registerChannel("TELLINFO");
        getProxy().registerChannel("GROUPUPDATE");

    }

    private ProxyServer getProxy() {
        return ProxyServer.getInstance();
    }

    @Override
    public void onDisable() {
        this.motdManager.save();
    }
}
