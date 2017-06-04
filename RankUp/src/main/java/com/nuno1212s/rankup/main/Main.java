package com.nuno1212s.rankup.main;

import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.rankup.economy.CoinCommand;
import com.nuno1212s.rankup.events.PlayerDisconnectListener;
import com.nuno1212s.rankup.events.PlayerJoinListener;
import com.nuno1212s.rankup.events.PlayerUpdateListener;
import com.nuno1212s.rankup.mysql.MySql;
import com.nuno1212s.rankup.playermanager.RUPlayerData;
import com.nuno1212s.util.ServerCurrencyHandler;
import lombok.Getter;

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

        registerServerEconomy();

        com.nuno1212s.displays.Main.getIns().getPlaceHolderManager().registerPlaceHolder("%coins%", (d) ->
                NumberFormat.getInstance().format(((RUPlayerData) d).getCoins())
        );

        registerCommand(new String[]{"coins", "coin"}, new CoinCommand());

        BukkitMain plugin = BukkitMain.getIns();

        plugin.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new PlayerUpdateListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new PlayerDisconnectListener(), plugin);
    }

    void registerServerEconomy() {
        MainData.getIns().setServerCurrencyHandler(new ServerCurrencyHandler() {
            @Override
            public long getCurrencyAmount(PlayerData playerData) {
                return ((RUPlayerData) playerData).getCoins();
            }

            @Override
            public boolean removeCurrency(PlayerData playerData, long amount) {
                RUPlayerData playerData1 = (RUPlayerData) playerData;
                if (playerData1.getCoins() > amount) {
                    playerData1.setCoins(playerData1.getCoins() - amount);
                    return true;
                }
                return false;
            }

            @Override
            public void addCurrency(PlayerData playerData, long amount) {
                ((RUPlayerData) playerData).setCoins(((RUPlayerData) playerData).getCoins() + amount);
            }

            @Override
            public boolean hasCurrency(PlayerData playerData, long amount) {
                RUPlayerData playerData1 = (RUPlayerData) playerData;
                return playerData1.getCoins() > amount;
            }
        });
    }

    @Override
    public void onDisable() {

    }
}