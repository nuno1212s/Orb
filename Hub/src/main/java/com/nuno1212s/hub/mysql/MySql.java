package com.nuno1212s.hub.mysql;

import com.nuno1212s.hub.playerdata.HPlayerData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;

import java.sql.*;

public class MySql {

    public MySql() {
        createTables();
    }

    public void createTables() {

        try (Connection c = MainData.getIns().getMySql().getConnection();
             Statement s = c.createStatement()) {

            String table = "CREATE TABLE IF NOT EXISTS hubPlayers(UUID char(40) PRIMARY KEY, CHATENABLED BOOL, PLAYERSSHOWN BOOL);";

            s.execute(table);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public HPlayerData getPlayerData(PlayerData data) {

        try (Connection c = MainData.getIns().getMySql().getConnection();
             PreparedStatement s = c.prepareStatement("SELECT * FROM hubPlayers WHERE UUID=?")) {

            s.setString(1, data.getPlayerID().toString());

            try (ResultSet resultSet = s.executeQuery()) {
                if (resultSet.next()) {
                    boolean chatEnabled = resultSet.getBoolean("CHATENABLED");
                    boolean playersShown = resultSet.getBoolean("PLAYERSSHOWN");

                    return new HPlayerData(data, chatEnabled, playersShown);
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new HPlayerData(data, true, false);
    }

    public void savePlayerData(HPlayerData data) {
        try (Connection c = MainData.getIns().getMySql().getConnection();
             PreparedStatement s = c.prepareStatement("INSERT INTO hubPlayers(UUID, CHATENABLED, PLAYERSSHOWN) values(?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE CHATENABLED=?, PLAYERSSHOWN=?")) {

            s.setString(1, data.getPlayerID().toString());
            s.setBoolean(2, data.isChatEnabled());
            s.setBoolean(3, data.isPlayerShown());
            s.setBoolean(4, data.isChatEnabled());
            s.setBoolean(5, data.isPlayerShown());

            s.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
