package com.nuno1212s.homes.main;

import com.nuno1212s.homes.commands.DelHomeCommand;
import com.nuno1212s.homes.commands.HomeCommand;
import com.nuno1212s.homes.commands.SetHomeCommand;
import com.nuno1212s.homes.filesystem.FileManager;
import com.nuno1212s.homes.homemanager.HomeManager;
import com.nuno1212s.homes.listeners.PlayerJoinListener;
import com.nuno1212s.homes.listeners.PlayerQuitListener;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;

/**
 * Homes main class
 */
@ModuleData(name = "Homes", version = "1.0", dependencies = {})
public class Main extends Module{

    @Getter
    static Main ins;

    @Getter
    private HomeManager homeManager;

    @Getter
    private FileManager fileManager;

    @Override
    public void onEnable() {
        ins = this;
        this.fileManager = new FileManager(this);
        this.homeManager = new HomeManager();

        BukkitMain ins = BukkitMain.getIns();

        registerCommand(new String[]{"home"}, new HomeCommand());
        registerCommand(new String[]{"sethome"}, new SetHomeCommand());
        registerCommand(new String[]{"delhome"}, new DelHomeCommand());

        ins.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), ins);

    }

    @Override
    public void onDisable() {

    }

}
