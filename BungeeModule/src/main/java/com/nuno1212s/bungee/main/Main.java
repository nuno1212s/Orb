package com.nuno1212s.bungee.main;

import com.google.common.cache.CacheBuilder;
import com.nuno1212s.bungee.automessenger.AutoMessageManager;
import com.nuno1212s.bungee.automessenger.commands.MessageCommand;
import com.nuno1212s.bungee.commands.*;
import com.nuno1212s.bungee.events.LoginEvent;
import com.nuno1212s.bungee.events.PermissionCheckListener;
import com.nuno1212s.bungee.events.QuitEvent;
import com.nuno1212s.bungee.loginhandler.MojangAPIConnector;
import com.nuno1212s.bungee.loginhandler.events.PlayerLoginEvent;
import com.nuno1212s.bungee.loginhandler.events.PluginMessageListener;
import com.nuno1212s.bungee.motd.MOTDCommand;
import com.nuno1212s.bungee.motd.ServerMOTD;
import com.nuno1212s.bungee.redishandler.PRedisListener;
import com.nuno1212s.bungee.redishandler.RedisListener;
import com.nuno1212s.config.BungeeConfig;
import com.nuno1212s.config.Config;
import com.nuno1212s.main.BungeeMain;
import com.nuno1212s.main.MainData;
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
    private AutoMessageManager autoMessageManager;

    @Getter
    private ServerMOTD motdManager;

    @Override
    public void onEnable() {
        ins = this;
        plugin = BungeeMain.getIns();

        Config c = new BungeeConfig(BungeeMain.getIns(), new File(this.getDataFolder(), "config.yml"));

        motdManager = new ServerMOTD(this.getDataFolder());
        autoMessageManager = new AutoMessageManager(this);
        getProxy().getPluginManager().registerListener(plugin, motdManager);
        getProxy().getPluginManager().registerListener(plugin, new LoginEvent());
        getProxy().getPluginManager().registerListener(plugin, new PlayerLoginEvent(this));
        getProxy().getPluginManager().registerListener(plugin, new PluginMessageListener());
        getProxy().getPluginManager().registerListener(plugin, new QuitEvent());
        getProxy().getPluginManager().registerListener(plugin, new PermissionCheckListener());

        getProxy().getPluginManager().registerCommand(plugin, new MOTDCommand());
        getProxy().getPluginManager().registerCommand(plugin, new RCommand());
        getProxy().getPluginManager().registerCommand(plugin, new StaffCommand());
        getProxy().getPluginManager().registerCommand(plugin, new TellCommand());
        getProxy().getPluginManager().registerCommand(plugin, new StaffChatCommand());
        getProxy().getPluginManager().registerCommand(plugin, new MessageCommand());
        getProxy().getPluginManager().registerCommand(plugin, new AlertCommand());
        getProxy().getPluginManager().registerCommand(plugin, new ActivateAutoLogin());

        ConcurrentMap<Object, Object> requestCache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES).build().asMap();

        List<String> stringList = c.get("ip-addresses") == null ? new ArrayList<>() : (List<String>) c.get("ip-addresses");

        connector = new MojangAPIConnector(requestCache, getProxy().getLogger(), stringList, 600);

        MainData.getIns().getMessageManager().addMessageFile(getFile("messages.json", true));

        System.out.println(stringList);

        MainData.getIns().getRedisHandler().registerRedisListener(new RedisListener());
        MainData.getIns().getRedisHandler().registerRedisListener(new PRedisListener());

        getProxy().registerChannel("AUTOLOGIN");

    }

    private ProxyServer getProxy() {
        return ProxyServer.getInstance();
    }

    @Override
    public void onDisable() {
        System.out.println("Saving");
        this.autoMessageManager.save();
        this.motdManager.save();
    }
}
