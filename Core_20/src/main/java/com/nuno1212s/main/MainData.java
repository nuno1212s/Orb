package com.nuno1212s.main;

import com.nuno1212s.command.CommandRegister;
import com.nuno1212s.messagemanager.Messages;
import com.nuno1212s.modulemanager.ModuleManager;
import com.nuno1212s.mysql.MySql;
import com.nuno1212s.permissionmanager.PermissionManager;
import com.nuno1212s.playermanager.PlayerManager;
import com.nuno1212s.scheduler.Scheduler;
import com.nuno1212s.serverstatus.ServerManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

/**
 * MainM class
 */
public class MainData {

    @Getter
    private static MainData ins;

    @Getter
    @Setter(value = AccessLevel.PROTECTED)
    private ModuleManager moduleManager;

    @Getter
    @Setter(value = AccessLevel.PROTECTED)
    private PermissionManager permissionManager;

    @Getter
    @Setter(value = AccessLevel.PROTECTED)
    private PlayerManager playerManager;

    @Getter
    @Setter(value = AccessLevel.PROTECTED)
    private ServerManager serverManager;

    @Getter
    @Setter(value = AccessLevel.PROTECTED)
    private MySql mySql;

    @Getter
    @Setter(value = AccessLevel.PROTECTED)
    private Scheduler scheduler;

    @Getter
    @Setter(value = AccessLevel.PROTECTED)
    private File dataFolder;

    @Getter
    @Setter(value = AccessLevel.PROTECTED)
    private Messages messageManager;

    @Getter
    @Setter(value = AccessLevel.PROTECTED)
    private CommandRegister commandRegister;

    @Getter
    @Setter(value = AccessLevel.PROTECTED)
    private boolean isBungee = false;

    public MainData() {
        ins = this;
    }


}