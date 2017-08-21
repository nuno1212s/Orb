package com.nuno1212s.fullpvp.main;

import com.nuno1212s.fullpvp.economy.CoinCommand;
import com.nuno1212s.fullpvp.events.PlayerJoinListener;
import com.nuno1212s.fullpvp.events.PlayerUpdateListener;
import com.nuno1212s.fullpvp.mysql.MySql;
import com.nuno1212s.fullpvp.playermanager.PVPPlayerData;
import com.nuno1212s.fullpvp.scoreboard.ScoreboardManager;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Full PVP Module Main Class
 */
@ModuleData(name = "Full PvP", version = "1.1-SNAPSHOT", dependencies = {"Crates"})
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

        MainData.getIns().getMessageManager().addMessageFile(getFile("messages.json", true));

        scoreboardManager = new ScoreboardManager(getFile("scoreboard.json", true));

        registerCommand(new String[]{"coins", "coin"}, new CoinCommand());

        /*com.nuno1212s.crates.Main.getIns().setServerEconomyInterface((player, cost) -> {
            PVPPlayerData playerData = (PVPPlayerData) MainData.getIns().getPlayerManager().getPlayer(player);
            if (playerData.getCoins() >= cost) {
                playerData.setCoins(playerData.getCoins() - cost);
                return true;
            }
            return false;
        });*/

        Plugin plugin = BukkitMain.getIns();
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerUpdateListener(), plugin);
    }

    @Override
    public void onDisable() {
    }
}
