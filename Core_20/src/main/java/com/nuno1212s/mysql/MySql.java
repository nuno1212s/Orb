package com.nuno1212s.mysql;

import com.nuno1212s.config.Config;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.permissionmanager.GroupType;
import com.nuno1212s.permissionmanager.PermissionManager;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;
import com.nuno1212s.playermanager.CorePlayerData;
import com.nuno1212s.playermanager.PlayerData;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.*;

/**
 * Handles mysql database connections
 */
public class MySql {

    private String host, username, password, database;

    private int port;

    private HikariDataSource dataSource;

    public MySql(Config config) {
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
        try (Connection c = getConnection();
             Statement st = c.createStatement()) {

            String stm = "CREATE TABLE IF NOT EXISTS playerData(UUID char(40) NOT NULL PRIMARY KEY, " +
                    "GROUPDATA varchar(100), " +
                    "PLAYERNAME varchar(16), " +
                    "PREMIUM BOOL," +
                    "LASTLOGIN TIMESTAMP," +
                    "TELL BOOL," +
                    "CASH BIGINT)";

            st.execute(stm);

            String stm2 = "CREATE TABLE IF NOT EXISTS groupData(GROUPID SMALLINT, GROUPNAME varchar(25)," +
                    "PREFIX varchar(32), SUFFIX varchar(32)," +
                    "SCOREBOARD varchar(32)," +
                    "ISDEFAULT BOOL," +
                    "APPLICABLESERVER varchar(25)," +
                    "GROUPTYPE varchar(6)," +
                    "PERMISSIONS TEXT)";

            st.execute(stm2);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PlayerData getPlayerData(UUID playerID, String playerName) {

        try (Connection c = getConnection();
             PreparedStatement select =
                     (playerName == null ?
                             c.prepareStatement("SELECT GROUPDATA, PLAYERNAME, CASH, PREMIUM, LASTLOGIN, TELL FROM playerData WHERE UUID=? LIMIT 1") :
                             c.prepareStatement("SELECT GROUPDATA, CASH, PREMIUM, LASTLOGIN, TELL FROM playerData WHERE UUID=? LIMIT 1"))
        ) {
            if (playerName == null) {
                select.setString(1, playerID.toString());
                try (ResultSet resultSet = select.executeQuery()) {
                    if (resultSet.next()) {
                        String groupid = resultSet.getString("GROUPDATA");
                        long cash = resultSet.getLong("CASH");
                        playerName = resultSet.getString("PLAYERNAME");
                        boolean premium = resultSet.getBoolean("PREMIUM");
                        long lastLogin = resultSet.getDate("LASTLOGIN").getTime();
                        boolean tell = resultSet.getBoolean("TELL");
                        PlayerData playerData = new CorePlayerData(playerID, new PlayerGroupData(groupid), playerName, cash, lastLogin, premium);
                        playerData.setTell(tell);
                        return playerData;
                    }
                }
            } else {
                select.setString(1, playerID.toString());
                try (ResultSet resultSet = select.executeQuery()) {
                    if (resultSet.next()) {
                        String groupid = resultSet.getString("GROUPDATA");
                        long cash = resultSet.getLong("CASH");
                        boolean premium = resultSet.getBoolean("PREMIUM");
                        long lastLogin = resultSet.getDate("LASTLOGIN").getTime();
                        boolean tell = resultSet.getBoolean("TELL");
                        PlayerData playerData = new CorePlayerData(playerID, new PlayerGroupData(groupid), playerName, cash, lastLogin, premium);
                        playerData.setTell(tell);
                        return playerData;
                    }
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public PlayerData getPlayerData(String playerName) {

        try (Connection c = getConnection();
             PreparedStatement select =
                     c.prepareStatement("SELECT UUID, GROUPDATA, PLAYERNAME, CASH, PREMIUM, LASTLOGIN, TELL FROM playerData WHERE playerName=? LIMIT 1")
        ) {
            select.setString(1, playerName);
            try (ResultSet resultSet = select.executeQuery()) {
                if (resultSet.next()) {
                    UUID playerID = UUID.fromString(resultSet.getString("UUID"));
                    String groupid = resultSet.getString("GROUPDATA");
                    long cash = resultSet.getLong("CASH");
                    boolean premium = resultSet.getBoolean("PREMIUM");
                    long lastLogin = resultSet.getDate("LASTLOGIN").getTime();
                    boolean tell = resultSet.getBoolean("TELL");
                    PlayerData playerData = new CorePlayerData(playerID, new PlayerGroupData(groupid), playerName, cash, lastLogin, premium);
                    playerData.setTell(tell);
                    return playerData;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void savePlayer(PlayerData d) {

        try (Connection c = getConnection();
             PreparedStatement st = c.prepareStatement("INSERT INTO playerData (UUID, GROUPDATA, PLAYERNAME, CASH, PREMIUM, LASTLOGIN, TELL) values(?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?) " +
                     "ON DUPLICATE KEY UPDATE GROUPDATA=?, CASH=?, PLAYERNAME=?, LASTLOGIN=CURRENT_TIMESTAMP, PREMIUM=?, TELL=?")) {

            st.setString(1, d.getPlayerID().toString());
            st.setString(2, d.getGroups().toDatabase());
            st.setString(3, d.getPlayerName());
            st.setLong(4, d.getCash());
            st.setBoolean(5, d.isPremium());
            st.setBoolean(6, d.isTell());
            st.setString(7, d.getGroups().toDatabase());
            st.setLong(8, d.getCash());
            st.setString(9, d.getPlayerName());
            st.setBoolean(10, d.isPremium());
            st.setBoolean(11, d.isTell());

            st.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Group> getGroups() {

        try (Connection c = getConnection();
             PreparedStatement st = c.prepareStatement("SELECT * FROM groupData");
             ResultSet resultSet = st.executeQuery()) {

            List<Group> groups = new ArrayList<>();

            while (resultSet.next()) {
                short groupID = resultSet.getShort("GROUPID");
                String groupName = resultSet.getString("GROUPNAME");
                String prefix = resultSet.getString("PREFIX");
                String suffix = resultSet.getString("SUFFIX");
                String scoreboardName = resultSet.getString("SCOREBOARD");
                boolean isDefault = resultSet.getBoolean("ISDEFAULT");
                String applicableServer = resultSet.getString("APPLICABLESERVER");
                GroupType type = GroupType.valueOf(resultSet.getString("GROUPTYPE"));
                String permissions1 = resultSet.getString("PERMISSIONS");
                List<String> permissions;
                if (!permissions1.equalsIgnoreCase("")) {
                    permissions = Arrays.asList(permissions1.split(","));
                } else {
                    permissions = new ArrayList<>();
                }
                Group e = new Group(groupID, groupName, prefix, suffix, scoreboardName, applicableServer, isDefault, type, permissions);
                if (PermissionManager.isApplicable(e)) {
                    groups.add(e);
                }
            }

            return groups;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

}
