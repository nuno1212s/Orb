package com.nuno1212s.fullpvp.main;

import com.nuno1212s.fullpvp.events.PlayerUpdateListener;
import com.nuno1212s.fullpvp.mysql.MySql;
import com.nuno1212s.fullpvp.economy.CoinCommand;
import com.nuno1212s.fullpvp.events.PlayerJoinListener;
import com.nuno1212s.fullpvp.scoreboard.ScoreboardManager;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.File;

/**
 * Full PVP Module Main Class
 */
@ModuleData(name = "Full PvP", version = "0.1-SNAPSHOT", dependencies = {})
public class Main extends Module {


    @Getter
    static Main ins;

    @Getter
    MySql mysql;

    @Getter
    ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        ins = this;
        mysql = new MySql();
        mysql.createTables();

        File file = new File(this.getDataFolder(), "scoreboard.json");
        if (!file.exists()) {
            com.nuno1212s.main.Main.getIns().saveResource("scoreboard.json", false);
        }

        scoreboardManager = new ScoreboardManager(file);

        registerCommand(new String[]{"coins", "coin"}, new CoinCommand());

        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), com.nuno1212s.main.Main.getIns());
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerUpdateListener(), com.nuno1212s.main.Main.getIns());

    }

    @Override
    public void onDisable() {

    }
}
