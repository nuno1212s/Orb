package com.nuno1212s.rankup.mysql;

import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.rankup.main.Main;
import com.nuno1212s.rankup.playermanager.RUPlayerData;
import com.nuno1212s.rankup.rankup.RankUpManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.*;
import java.util.*;

/**
 * mysql data class
 */
public class MySql {

    public void createTables() {

        try (Connection c = MainData.getIns().getMySql().getConnection();
             Statement s = c.createStatement()) {
            String pvpData = "CREATE TABLE IF NOT EXISTS pvpData(UUID char(40) PRIMARY KEY, COINS BIGINT, GROUPDATA varchar(100), SERVERGROUP varchar(100), KITUSAGE varchar(200))";
            s.execute(pvpData);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public RUPlayerData loadPlayerData(PlayerData playerData) {

        try (Connection c = MainData.getIns().getMySql().getConnection();
             PreparedStatement st = c.prepareStatement("SELECT * FROM pvpData WHERE UUID=? LIMIT 1")) {
            st.setString(1, playerData.getPlayerID().toString());

            try (ResultSet resultSet = st.executeQuery()) {
                if (resultSet.next()) {

                    long coins = resultSet.getLong("COINS");
                    PlayerGroupData groupData = new PlayerGroupData(resultSet.getString("GROUPDATA"));
                    String servergroup = resultSet.getString("SERVERGROUP");

                    PlayerGroupData serverData;
                    if (servergroup.equalsIgnoreCase("")) {
                        serverData = new PlayerGroupData();
                    } else {
                        serverData = new PlayerGroupData(servergroup);
                    }

                    String kitUsage = resultSet.getString("KITUSAGE");

                    JSONObject jsonObject = (JSONObject) new JSONParser().parse(kitUsage);
                    Map<Integer, Long> kitUsages = new HashMap<>(jsonObject.size());
                    jsonObject.forEach((key, value) -> {
                        int kitID = Integer.parseInt((String) key);
                        long lastUsage = (Long) value;
                        kitUsages.put(kitID, lastUsage);
                    });

                    return new RUPlayerData(playerData, coins, groupData, serverData, kitUsages, new ArrayList<>());
                }
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }

        short firstGroup = Main.getIns().getRankUpManager().getFirstGroup();

        return new RUPlayerData(playerData, 0, new PlayerGroupData(), new PlayerGroupData(firstGroup), new HashMap<>(), new ArrayList<>());
    }

    public LinkedHashMap<UUID, Long> getCoinTop(int limit) {
        try (Connection c = MainData.getIns().getMySql().getConnection();
             PreparedStatement st = c.prepareStatement("SELECT UUID, COINS FROM pvpData ORDER BY COINS LIMIT ?")) {

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

    public void savePlayerData(RUPlayerData playerData) {

        try (Connection c = MainData.getIns().getMySql().getConnection();
             PreparedStatement st = c.prepareStatement("INSERT INTO pvpData(UUID, COINS, GROUPDATA, SERVERGROUP, KITUSAGE) values(?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE COINS=?, GROUPDATA=?, KITUSAGE=?, SERVERGROUP=?")) {
            st.setString(1, playerData.getPlayerID().toString());
            st.setLong(2, playerData.getCoins());
            st.setString(3, playerData.getGroupData().toDatabase());
            st.setString(4, playerData.getRankUpGroupData().toDatabase());
            JSONObject jsonObject = new JSONObject();
            playerData.getKitUsages().forEach((kitID, lastUsage) ->
                    jsonObject.put(String.valueOf(kitID), lastUsage)
            );
            String kits = jsonObject.toJSONString();
            st.setString(5, kits);
            st.setLong(6, playerData.getCoins());
            st.setString(7, playerData.getGroupData().toDatabase());
            st.setString(8, kits);
            st.setString(9, playerData.getRankUpGroupData().toDatabase());
            st.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
