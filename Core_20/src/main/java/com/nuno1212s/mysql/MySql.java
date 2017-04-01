package com.nuno1212s.mysql;

import com.nuno1212s.main.Main;
import com.nuno1212s.playermanager.PlayerData;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Cleanup;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles mysql database connections
 */
public class MySql {

    private String host, username, password, database;

    private int port;

    private HikariDataSource dataSource;

    private Main m;

    public MySql(Main m) {
        this.m = m;
        FileConfiguration config = this.m.getConfig();
        this.host = config.getString("Host");
        this.username = config.getString("Username");
        this.database = config.getString("Database");
        this.password = config.getString("Password");
        this.port = config.getInt("Port");
        openConnection();
    }

    private void openConnection() {
        HikariConfig cfg = new HikariConfig();
        cfg.setUsername(username);
        cfg.setPassword(password);
        cfg.addDataSourceProperty("databaseName", database);
        cfg.setJdbcUrl("jdbc:mysql://" + host + ":" + String.valueOf(port) + "/" + database);
        cfg.setMaximumPoolSize(35);
        this.dataSource = new HikariDataSource(cfg);
        createTables();
    }

    public void closeConnection() {
        dataSource.close();
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void closeConnection(Connection c) {
        if (c != null) {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void createTables() {
        @Cleanup
        Connection c;
        try {
            c = getConnection();

            Statement st = c.createStatement();

            String stm = "CREATE TABLE IF NOT EXISTS playerData(UUID char(40) NOT NULL PRIMARY KEY, " +
                    "GROUPID SMALLINT, " +
                    "PLAYERNAME varchar(25), " +
                    "CASH BIGINT)";

            st.execute(stm);

            String stm2 = "CREATE TABLE IF NOT EXISTS groupData(GROUPID SMALLINT, GROUPNAME varchar(25)," +
                    "PREFIX varchar(32), SUFFIX varchar(32)," +
                    " SCOREBOARD varchar(32), GROUPTYPE varchar(6)," +
                    " PERMISSIONS TEXT)";

            st.execute(stm2);

            st.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PlayerData getPlayerData(UUID playerID, String playerName) {

        @Cleanup
        Connection c;

        try {
            c = getConnection();
            if (playerName == null) {
                @Cleanup
                PreparedStatement select = c.prepareStatement("SELECT GROUPID, PLAYERNAME, CASH FROM playerData WHERE UUID=?");
                select.setString(1, playerID.toString());
                @Cleanup
                ResultSet resultSet = select.executeQuery();
                if (resultSet.next()) {
                    short groupid = resultSet.getShort("GROUPID");
                    long cash = resultSet.getLong("CASH");
                    playerName = resultSet.getString("PLAYERNAME");
                    return new PlayerData(playerID, groupid, playerName, cash);
                }
            } else {
                @Cleanup
                PreparedStatement select = c.prepareStatement("SELECT GROUPID, CASH FROM playerData WHERE UUID=?");
                select.setString(1, playerID.toString());
                @Cleanup
                ResultSet resultSet = select.executeQuery();
                if (resultSet.next()) {
                    short groupid = resultSet.getShort("GROUPID");
                    long cash = resultSet.getLong("CASH");
                    return new PlayerData(playerID, groupid, playerName, cash);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void savePlayer(PlayerData d) {

        @Cleanup
        Connection c;

        try {
            c = getConnection();

            PreparedStatement st = c.prepareStatement("INSERT INTO playerData (UUID, GROUPID, PLAYERNAME, CASH) values(?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE GROUPID=?, CASH=?, PLAYERNAME=?");

            st.setString(1, d.getPlayerID().toString());
            st.setShort(2, d.getGroupID());
            st.setString(3, d.getPlayerName());
            st.setLong(4, d.getCash());
            st.setShort(5, d.getGroupID());
            st.setLong(6, d.getCash());
            st.setString(7, d.getPlayerName());

            st.executeUpdate();

            st.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
