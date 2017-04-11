package com.nuno1212s.fullpvp.mysql;

import com.nuno1212s.fullpvp.playermanager.PVPPlayerData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;

import java.sql.*;

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
