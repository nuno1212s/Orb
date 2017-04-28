package com.nuno1212s.rankup.mysql;

import com.nuno1212s.rankup.playermanager.PVPPlayerData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * mysql data class
 */
public class MySql {

    public void createTables() {

        try (Connection c = MainData.getIns().getMySql().getConnection();
             Statement s = c.createStatement())
        {
            String pvpData = "CREATE TABLE IF NOT EXISTS pvpData(UUID char(40) PRIMARY KEY, COINS BIGINT, GROUPDATA varchar(100))";
            s.execute(pvpData);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void loadPlayerData(PVPPlayerData playerData) {

        try (Connection c = MainData.getIns().getMySql().getConnection();
             PreparedStatement st = c.prepareStatement("SELECT * FROM pvpData WHERE UUID=? LIMIT 1"))
        {
            st.setString(1, playerData.getPlayerID().toString());
            ResultSet resultSet = st.executeQuery();
            if (resultSet.next()) {
                playerData.setCoins(resultSet.getLong("COINS"));
                playerData.setGroupData(new PlayerGroupData(resultSet.getString("GROUPDATA")));
            }
            resultSet.close();
        } catch (SQLException e) {
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

    public void savePlayerData(PVPPlayerData playerData) {

        try (Connection c = MainData.getIns().getMySql().getConnection();
            PreparedStatement st = c.prepareStatement("INSERT INTO pvpData(UUID, COINS, GROUPDATA) values(?, ?, ?) ON DUPLICATE KEY UPDATE COINS=?, GROUPDATA=?"))
        {
            st.setString(1, playerData.getPlayerID().toString());
            st.setLong(2, playerData.getCoins());
            st.setString(3, playerData.getGroupData().toDatabase());
            st.setLong(4, playerData.getCoins());
            st.setString(5, playerData.getGroupData().toDatabase());
            st.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
