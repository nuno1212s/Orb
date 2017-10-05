package com.nuno1212s.main;

import com.nuno1212s.command.*;
import com.nuno1212s.config.BukkitConfig;
import com.nuno1212s.events.PlayerInformationUpdateEvent;
import com.nuno1212s.events.eventcaller.EventCaller;
import com.nuno1212s.events.listeners.PlayerDisconnectListener;
import com.nuno1212s.events.listeners.PlayerJoinListener;
import com.nuno1212s.messagemanager.Messages;
import com.nuno1212s.modulemanager.ModuleManager;
import com.nuno1212s.mysql.MySql;
import com.nuno1212s.permissionmanager.PermissionManager;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.playermanager.PlayerManager;
import com.nuno1212s.rediscommunication.RedisHandler;
import com.nuno1212s.rewards.bukkit.BukkitRewardManager;
import com.nuno1212s.scheduler.BukkitScheduler;
import com.nuno1212s.serverstatus.ServerManager;
import com.nuno1212s.server_sender.BukkitSender;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Main plugin class
 */
public class BukkitMain extends JavaPlugin {

    @Getter
    static BukkitMain ins;

    static {
        //PRESTART
        System.out.println("STARTING CORE");
    }

    @Override
    public void onEnable() {
        ins = this;
        MainData data = new MainData();

        this.saveDefaultConfig();
        data.setEventCaller(new EventCaller() {
            @Override
            public void callUpdateInformationEvent(PlayerData data) {
                Bukkit.getServer().getPluginManager().callEvent(new PlayerInformationUpdateEvent(data));
            }
        });
        data.setDataFolder(this.getDataFolder());
        BukkitConfig config = new BukkitConfig(this.getConfig());
        data.setScheduler(new BukkitScheduler(this.getServer().getScheduler(), this));
        data.setMySql(new MySql(config));
        data.setRedisHandler(new RedisHandler(config));
        data.setServerManager(new ServerManager(this.getDataFolder()));
        data.setPermissionManager(new PermissionManager(true));
        data.setPlayerManager(new PlayerManager());

        data.setCommandRegister(new CommandRegister() {
            @Override
            public void registerCommand(String[] aliases, Object commandExecutor) {
                register(aliases);
                getCommand(aliases[0]).setExecutor((CommandExecutor) commandExecutor);
            }
        });

        File j = new File(this.getDataFolder(), "messages.json");
        if (!j.exists()) {
            this.saveResource("messages.json", false);
        }

        data.setMessageManager(new Messages(j));
        data.setRewardManager(new BukkitRewardManager());
        new BukkitSender(this);
        //Module manager has to be the last thing that is initialized
        data.setModuleManager(new ModuleManager(this.getDataFolder(), this.getClassLoader()));
        data.getMessageManager().reloadMessages();

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDisconnectListener(), this);

        MainData.getIns().getCommandRegister().registerCommand(new String[]{"cash"}, new CashCommand());
        MainData.getIns().getCommandRegister().registerCommand(new String[]{"reloadmessages"}, new ReloadMessages());
        MainData.getIns().getCommandRegister().registerCommand(new String[]{"server"}, new ServerSettingCommand());
        MainData.getIns().getCommandRegister().registerCommand(new String[]{"group"}, new GroupCommand());
        MainData.getIns().getCommandRegister().registerCommand(new String[]{"server"}, new ServerCommand());

        data.getServerManager().savePlayerCount(0, getServer().getMaxPlayers());
    }

    @Override
    public void onDisable() {
        MainData ins = MainData.getIns();
        ins.getServerManager().save();
        ins.getModuleManager().disable();
        ins.getMySql().closeConnection();
    }

    /**
     * Register a command with the given aliases
     *
     * @param aliases
     */
    public void register(String... aliases) {
        PluginCommand command = getCommand(aliases[0], this);

        command.setAliases(Arrays.asList(aliases));
        getCommandMap().register(this.getDescription().getName(), command);
    }


    private static PluginCommand getCommand(String name, Plugin plugin) {
        PluginCommand command = null;

        try {
            Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            c.setAccessible(true);

            command = c.newInstance(name, plugin);
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException
                | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return command;
    }

    private static CommandMap getCommandMap() {
        CommandMap commandMap = null;

        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                Field f = SimplePluginManager.class.getDeclaredField("commandMap");
                f.setAccessible(true);

                commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
            }
        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return commandMap;
    }

}
