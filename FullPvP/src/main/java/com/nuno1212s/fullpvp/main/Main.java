package com.nuno1212s.fullpvp.main;

import com.nuno1212s.fullpvp.crates.CrateManager;
import com.nuno1212s.fullpvp.crates.commands.CrateCommandManager;
import com.nuno1212s.fullpvp.events.PlayerUpdateListener;
import com.nuno1212s.fullpvp.events.animations.*;
import com.nuno1212s.fullpvp.mysql.MySql;
import com.nuno1212s.fullpvp.economy.CoinCommand;
import com.nuno1212s.fullpvp.events.PlayerJoinListener;
import com.nuno1212s.fullpvp.scoreboard.ScoreboardManager;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;

/**
 * Full PVP Module Main Class
 */
@ModuleData(name = "Full PvP", version = "1.1-SNAPSHOT", dependencies = {})
public class Main extends Module {

    @Getter
    static Main ins;

    @Getter
    MySql mysql;

    @Getter
    ScoreboardManager scoreboardManager;

    @Getter
    CrateManager crateManager;

    @Override
    public void onEnable() {
        ins = this;
        mysql = new MySql();
        mysql.createTables();

        MainData.getIns().getMessageManager().addMessageFile(getFile("messages.json", true));

        scoreboardManager = new ScoreboardManager(getFile("scoreboard.json", true));
        crateManager = new CrateManager(this);

        registerCommand(new String[]{"crate"}, new CrateCommandManager());

        registerCommand(new String[]{"coins", "coin"}, new CoinCommand());

        Plugin plugin = com.nuno1212s.main.Main.getIns();
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerUpdateListener(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerCloseInventoryListener(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryClickEventListener(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteractListener(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerBreakBlockListener(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerChangeItemNameListener(), plugin);
    }

    @Override
    public void onDisable() {
        crateManager.save();
    }
}
