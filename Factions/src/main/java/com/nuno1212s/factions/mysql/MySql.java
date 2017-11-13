package com.nuno1212s.factions.mysql;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nuno1212s.enderchest.playerdata.EnderChestData;
import com.nuno1212s.factions.playerdata.FPlayerData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;
import com.nuno1212s.playermanager.PlayerData;

import java.sql.*;
import java.util.*;

public class MySql {

    private Gson gson;

    public MySql() {
        gson = new GsonBuilder().create();
        try (Connection c = MainData.getIns().getMySql().getConnection();
             Statement s = c.createStatement()) {
            String factionTable = "CREATE TABLE IF NOT EXISTS factionsPlayers(UUID CHAR(40) NOT NULL PRIMARY KEY, COINS BIGINT, GROUPDATA varchar(100), KITUSAGE varchar(200), ENDERCHEST MEDIUMTEXT NOT NULL)";

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

                    String enderChest = resultSet.getString("ENDERCHEST");

                    FPlayerData playerData = new FPlayerData(d, data, coins, kits, new ArrayList<>(), enderChest);
                    return playerData;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new FPlayerData(d, new PlayerGroupData(), 0, new HashMap<>(), new ArrayList<>(), "");
    }

    public LinkedHashMap<UUID, Long> getCoinTop(int limit) {
        try (Connection c = MainData.getIns().getMySql().getConnection();
             PreparedStatement st = c.prepareStatement("SELECT UUID, COINS FROM factionsPlayers ORDER BY COINS DESC LIMIT ?")) {

            st.setInt(1, limit);

            try (ResultSet resultSet = st.executeQuery()) {

                LinkedHashMap<UUID, Long> players = new LinkedHashMap<>();

                while (resultSet.next()) {
                    players.put(UUID.fromString(resultSet.getString("UUID")), resultSet.getLong("COINS"));
                }

                return players;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void savePlayerData(FPlayerData d) {
        try (Connection c = MainData.getIns().getMySql().getConnection();
            PreparedStatement s = c.prepareStatement("INSERT INTO factionsPlayers(UUID, GROUPDATA, COINS, KITUSAGE, ENDERCHEST) values(?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE GROUPDATA=?, COINS=?, KITUSAGE=?, ENDERCHEST=?")) {
            s.setString(1, d.getPlayerID().toString());
            s.setString(2, d.getServerGroupData().toDatabase());
            s.setLong(3, d.getCoins());
            s.setString(4, gson.toJson(d.getKitUsages()));
            String enderChest = EnderChestData.inventoryToJSON(d.getEnderChest());
            s.setString(5, enderChest);
            s.setString(6, d.getServerGroupData().toDatabase());
            s.setLong(7, d.getCoins());
            s.setString(8, gson.toJson(d.getKitUsages()));
            s.setString(9, enderChest);
            s.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
