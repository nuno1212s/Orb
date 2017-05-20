package com.nuno1212s.mercado.main;

import com.nuno1212s.mercado.database.MySql;
import com.nuno1212s.modulemanager.Module;
import lombok.Getter;

/**
 * Main module class
 */
public class Main extends Module {

    @Getter
    private static Main ins;

    @Getter
    private MySql mySql;

    @Override
    public void onEnable() {
        ins = this;
    }

    @Override
    public void onDisable() {

    }

}
