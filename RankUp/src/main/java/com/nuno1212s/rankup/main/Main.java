package com.nuno1212s.rankup.main;

import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import com.nuno1212s.rankup.crates.CrateManager;
import com.nuno1212s.rankup.crates.commands.CrateCommandManager;
import com.nuno1212s.rankup.economy.CoinCommand;
import com.nuno1212s.rankup.events.PlayerJoinListener;
import com.nuno1212s.rankup.events.PlayerUpdateListener;
import com.nuno1212s.rankup.events.animations.*;
import com.nuno1212s.rankup.mysql.MySql;
import com.nuno1212s.rankup.scoreboard.ScoreboardManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Main Class
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