package com.nuno1212s.main;

import com.nuno1212s.modulemanager.ModuleManager;
import com.nuno1212s.mysql.MySql;
import com.nuno1212s.permissionmanager.PermissionManager;
import com.nuno1212s.playermanager.PlayerManager;
import com.nuno1212s.scheduler.Scheduler;
import com.nuno1212s.serverstatus.ServerManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

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

    public MainData() {
        ins = this;
    }



}