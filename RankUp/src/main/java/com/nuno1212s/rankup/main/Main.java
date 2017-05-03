package com.nuno1212s.rankup.main;

import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import com.nuno1212s.rankup.economy.CoinCommand;
import com.nuno1212s.rankup.events.PlayerDisconnectListener;
import com.nuno1212s.rankup.events.PlayerJoinListener;
import com.nuno1212s.rankup.events.PlayerUpdateListener;
import com.nuno1212s.rankup.mysql.MySql;
import com.nuno1212s.rankup.playermanager.RUPlayerData;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.text.NumberFormat;


/**
 * Main Class
 */
@ModuleData(name = "RankUp", version = "1.1-SNAPSHOT", dependencies = {"Crates", "Displays", "Classes"})
public class Main extends Module {

    @Getter
    static Main ins;

    @Getter
    MySql mysql;

    @Override
    public void onEnable() {
        ins = this;
        mysql = new MySql();
        mysql.createTables();

        MainData.getIns().getMessageManager().addMessageFile(getFile("messages.json", true));

        com.nuno1212s.crates.Main.getIns().setServerEconomyInterface((player, cost) -> {
            RUPlayerData playerData = (RUPlayerData) MainData.getIns().getPlayerManager().getPlayer(player);
            if (playerData.getCoins() >= cost) {
                playerData.setCoins(playerData.getCoins() - cost);
                return true;
            }
            return false;
        });

        com.nuno1212s.displays.Main.getIns().getPlaceHolderManager().registerPlaceHolder("%coins%", (d) ->
                NumberFormat.getInstance().format(((RUPlayerData) d).getCoins())
        );

        registerCommand(new String[]{"coins", "coin"}, new CoinCommand());

        Plugin plugin = com.nuno1212s.main.Main.getIns();

        plugin.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new PlayerUpdateListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new PlayerDisconnectListener(), plugin);
    }

    @Override
    public void onDisable() {

    }
}