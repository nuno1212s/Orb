package com.nuno1212s.rankup.mysql;

import com.nuno1212s.rankup.playermanager.RUPlayerData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * mysql data class
 */
public class MySql {

    public void createTables() {

        try (Connection c = MainData.getIns().getMySql().getConnection();
             Statement s = c.createStatement())
        {
            String pvpData = "CREATE TABLE IF NOT EXISTS pvpData(UUID char(40) PRIMARY KEY, COINS BIGINT, GROUPDATA varchar(100), KITUSAGE varchar(200))";
            s.execute(pvpData);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void loadPlayerData(RUPlayerData playerData) {

        try (Connection c = MainData.getIns().getMySql().getConnection();
             PreparedStatement st = c.prepareStatement("SELECT * FROM pvpData WHERE UUID=? LIMIT 1"))
        {
            st.setString(1, playerData.getPlayerID().toString());
            ResultSet resultSet = st.executeQuery();
            if (resultSet.next()) {
                playerData.setCoins(resultSet.getLong("COINS"));
                playerData.setGroupData(new PlayerGroupData(resultSet.getString("GROUPDATA")));
                String kitUsage = resultSet.getString("KITUSAGE");
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(kitUsage);
                Map<Integer, Long> kits = new HashMap<>(jsonObject.size());
                jsonObject.forEach((key, value) -> {
                    int kitID = Integer.parseInt((String) key);
                    long lastUsage = (Long) value;
                    kits.put(kitID, lastUsage);
                });

                playerData.setKitUsages(kits);

            }
            resultSet.close();
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }

    }

    public LinkedHashMap<UUID, Long> getCoinTop(int limit) {
        try (Connection c = MainData.getIns().getMySql().getConnection();
            PreparedStatement st = c.prepareStatement("SELECT UUID, COINS FROM pvpData ORDER BY COINS LIMIT ?")) {
            st.setInt(1, limit);
            ResultSet resultSet = st.executeQuery();
            LinkedHashMap<UUID, Long> players = new LinkedHashMap<>();
            while (resultSet.next()) {
                players.put(UUID.fromString(resultSet.getString("UUID")), resultSet.getLong("COINS"));
            }
            return players;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void savePlayerData(RUPlayerData playerData) {

        try (Connection c = MainData.getIns().getMySql().getConnection();
            PreparedStatement st = c.prepareStatement("INSERT INTO pvpData(UUID, COINS, GROUPDATA, KITUSAGE) values(?, ?, ?, ?) ON DUPLICATE KEY UPDATE COINS=?, GROUPDATA=?, KITUSAGE=?"))
        {
            st.setString(1, playerData.getPlayerID().toString());
            st.setLong(2, playerData.getCoins());
            st.setString(3, playerData.getGroupData().toDatabase());
            JSONObject jsonObject = new JSONObject();
            playerData.getKitUsages().forEach((kitID, lastUsage) ->
                jsonObject.put(String.valueOf(kitID), lastUsage)
            );
            String kits = jsonObject.toJSONString();
            st.setString(4, kits);
            st.setLong(5, playerData.getCoins());
            st.setString(6, playerData.getGroupData().toDatabase());
            st.setString(7, kits);
            st.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
