package com.nuno1212s.factions.main;

import com.nuno1212s.factions.mysql.MySql;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;


@ModuleData(name = "Factions", version = "0.1")
public class Main extends Module {

    @Getter
    static Main ins;

    @Getter
    private MySql mysql;

    @Override
    public void onEnable() {
        ins = this;
        mysql = new MySql();
    }

    @Override
    public void onDisable() {

    }
}
