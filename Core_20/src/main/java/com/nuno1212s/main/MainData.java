package com.nuno1212s.main;

import com.nuno1212s.command.CommandRegister;
import com.nuno1212s.events.eventcaller.EventCaller;
import com.nuno1212s.messagemanager.Messages;
import com.nuno1212s.modulemanager.ModuleManager;
import com.nuno1212s.mysql.MySql;
import com.nuno1212s.permissionmanager.PermissionManager;
import com.nuno1212s.playermanager.PlayerManager;
import com.nuno1212s.rediscommunication.RedisHandler;
import com.nuno1212s.scheduler.Scheduler;
import com.nuno1212s.serverstatus.ServerManager;
import com.nuno1212s.util.ServerCurrencyHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

/**
 * MainM class
 */
@Getter
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

    private boolean isBungee = false;

    @Setter
    private ServerCurrencyHandler serverCurrencyHandler = null;

    public MainData() {
        ins = this;
    }

    public boolean hasServerCurrency() {
        return serverCurrencyHandler != null;
    }


}