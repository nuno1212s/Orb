package com.nuno1212s.factions.mysql;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nuno1212s.factions.playerdata.FPlayerData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;
import com.nuno1212s.playermanager.PlayerData;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MySql {

    private Gson gson;

    public MySql() {
        gson = new GsonBuilder().create();
        try (Connection c = MainData.getIns().getMySql().getConnection();
             Statement s = c.createStatement()) {
            String factionTable = "CREATE TABLE IF NOT EXISTS factionsPlayers(UUID CHAR(40) NOT NULL PRIMARY KEY, COINS BIGINT, GROUPDATA varchar(100), KITUSAGE varchar(200))";

            s.execute(factionTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public FPlayerData getPlayerData(PlayerData d) {
        try (Connection c = MainData.getIns().getMySql().getConnection();
             PreparedStatement s = c.prepareStatement("SELECT * FROM factionsPlayers WHERE UUID=?")) {

            s.setString(1, d.getPlayerID().toString());

            try (ResultSet resultSet = s.executeQuery()) {
                if (resultSet.next()) {
                    PlayerGroupData data = new PlayerGroupData(resultSet.getString("GROUPDATA"));
                    long coins = resultSet.getLong("COINS");

                    Map<Integer, Long> kits = gson.fromJson(resultSet.getString("KITUSAGE"), new TypeToken<HashMap<Integer, Long>>(){}.getType());

                    FPlayerData playerData = new FPlayerData(d, data, coins, kits, new ArrayList<>());
                    return playerData;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new FPlayerData(d, new PlayerGroupData(), 0, new HashMap<>(), new ArrayList<>());
    }

    public void savePlayerData(FPlayerData d) {
        try (Connection c = MainData.getIns().getMySql().getConnection();
            PreparedStatement s = c.prepareStatement("INSERT INTO factionsPlayers(UUID, GROUPDATA, COINS, KITUSAGE) values(?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE GROUPDATA=?, COINS=?, KITUSAGE=?")) {
            s.setString(1, d.getPlayerID().toString());
            s.setString(2, d.getServerGroupData().toDatabase());
            s.setLong(3, d.getCoins());
            s.setString(4, gson.toJson(d.getKitUsages()));
            s.setString(5, d.getServerGroupData().toDatabase());
            s.setLong(6, d.getCoins());
            s.setString(7, gson.toJson(d.getKitUsages()));
            s.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
