package com.nuno1212s.mysql;

import com.nuno1212s.main.Main;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.permissionmanager.GroupType;
import com.nuno1212s.permissionmanager.PermissionManager;
import com.nuno1212s.playermanager.PlayerData;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Cleanup;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.*;

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
        try (Connection c = getConnection();
             Statement st = c.createStatement()){

            String stm = "CREATE TABLE IF NOT EXISTS playerData(UUID char(40) NOT NULL PRIMARY KEY, " +
                    "GROUPID SMALLINT, " +
                    "PLAYERNAME varchar(16), " +
                    "PREMIUM BOOL," +
                    "LASTIP varchar(255)," +
                    "LASTLOGIN TIMESTAMP," +
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
                             c.prepareStatement("SELECT GROUPID, PLAYERNAME, CASH FROM playerData WHERE UUID=?") :
                             c.prepareStatement("SELECT GROUPID, CASH FROM playerData WHERE UUID=?"))
        ) {
            if (playerName == null) {
                select.setString(1, playerID.toString());
                try (ResultSet resultSet = select.executeQuery()) {
                    if (resultSet.next()) {
                        short groupid = resultSet.getShort("GROUPID");
                        long cash = resultSet.getLong("CASH");
                        playerName = resultSet.getString("PLAYERNAME");
                        return new PlayerData(playerID, groupid, playerName, cash);
                    }
                }
            } else {
                select.setString(1, playerID.toString());
                try (ResultSet resultSet = select.executeQuery()) {
                    if (resultSet.next()) {
                        short groupid = resultSet.getShort("GROUPID");
                        long cash = resultSet.getLong("CASH");
                        return new PlayerData(playerID, groupid, playerName, cash);
                    }
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void savePlayer(PlayerData d) {

        try (Connection c = getConnection();
             PreparedStatement st = c.prepareStatement("INSERT INTO playerData (UUID, GROUPID, PLAYERNAME, CASH) values(?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE GROUPID=?, CASH=?, PLAYERNAME=?")) {


            st.setString(1, d.getPlayerID().toString());
            st.setShort(2, d.getGroupID());
            st.setString(3, d.getPlayerName());
            st.setLong(4, d.getCash());
            st.setShort(5, d.getGroupID());
            st.setLong(6, d.getCash());
            st.setString(7, d.getPlayerName());

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
