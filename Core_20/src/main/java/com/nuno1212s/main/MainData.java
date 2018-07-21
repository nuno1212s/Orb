package com.nuno1212s.main;

import com.nuno1212s.command.CommandRegister;
import com.nuno1212s.economy.CurrencyHandler;
import com.nuno1212s.events.eventcaller.EventCaller;
import com.nuno1212s.inventories.InventoryManager;
import com.nuno1212s.messagemanager.Messages;
import com.nuno1212s.modulemanager.ModuleManager;
import com.nuno1212s.mysql.MySql;
import com.nuno1212s.permissionmanager.PermissionManager;
import com.nuno1212s.playermanager.PlayerManager;
import com.nuno1212s.rediscommunication.RedisHandler;
import com.nuno1212s.rewards.RewardManager;
import com.nuno1212s.scheduler.Scheduler;
import com.nuno1212s.serverstatus.ServerManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

/**
 * MainM class
 */

@Setter(value = AccessLevel.PROTECTED)
public class MainData {

    @Getter
    private static MainData ins;

    private ModuleManager moduleManager;

    private PermissionManager permissionManager;

    private PlayerManager playerManager;

    private ServerManager serverManager;

    private MySql mySql;

    private Scheduler scheduler;

    private File dataFolder;

    private Messages messageManager;

    private CommandRegister commandRegister;

    private EventCaller eventCaller;

    private RedisHandler redisHandler;

    private RewardManager rewardManager;

    private InventoryManager inventoryManager;

    private boolean isBungee = false;

    @Setter
    private CurrencyHandler serverCurrencyHandler = null;

    public MainData() {
        ins = this;
    }

    public boolean hasServerCurrency() {
        return serverCurrencyHandler != null;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public ServerManager getServerManager() {
        return serverManager;
    }

    public MySql getMySql() {
        return mySql;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public Messages getMessageManager() {
        return messageManager;
    }

    public CommandRegister getCommandRegister() {
        return commandRegister;
    }

    public EventCaller getEventCaller() {
        return eventCaller;
    }

    public RedisHandler getRedisHandler() {
        return redisHandler;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public boolean isBungee() {
        return isBungee;
    }

    public CurrencyHandler getServerCurrencyHandler() {
        return serverCurrencyHandler;
    }
}