package com.nuno1212s.mysql;

import com.nuno1212s.confighandler.Config;
import com.nuno1212s.permissions.PermissionsGroup;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Cleanup;
import net.md_5.bungee.api.ChatColor;
import com.nuno1212s.main.Main;
import com.nuno1212s.permissions.PermissionsGroupManager;
import com.nuno1212s.playermanager.PlayerData;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MySqlHandler {

    private String host, username, database;

    private char[] password;

    private int port;

    private Main m;

    private HikariDataSource connectionPool;

    private static MySqlHandler ins;

    public static MySqlHandler getIns() {
        return ins;
    }

    public MySqlHandler(Main m) {
        ins = this;
        this.m = m;
        loadData();
        openConnection();
        createTable();
    }

    public void loadData() {
        host = Config.getIns().getC().getString("Host");
        username = Config.getIns().getC().getString("Username");
        database = Config.getIns().getC().getString("Database");
        password = Config.getIns().getC().getString("Password").toCharArray();
        port = Config.getIns().getC().getInt("Port");
        System.out.println("Loaded mysql data...");
    }

    public void openConnection() {
        System.out.println("Opening mysql connection");
        HikariConfig cfg = new HikariConfig();
        cfg.setUsername(username);
        cfg.setPassword(new String(password));
        cfg.setJdbcUrl("jdbc:mysql://" + host + ":" + String.valueOf(port) + "/" + database);
        cfg.addDataSourceProperty("databaseName", database);
        cfg.setMaximumPoolSize(10);
        connectionPool = new HikariDataSource(cfg);
    }

    public void closeConnection() {
        connectionPool.close();
    }

    public Connection getConnection() {
        try {
            return this.connectionPool.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void createTable() {
        Connection c = null;
        try {
            c  = connectionPool.getConnection();
            Statement s = c.createStatement();
            String createTable = "CREATE TABLE IF NOT EXISTS playerData(UUID varchar(40) PRIMARY KEY" +
                    ", NAME varchar(18)" +
                    ", GROUPID TINYINT" +
                    ", PREMIUM BOOLEAN" +
                    ", LastIp VARCHAR(255)" +
                    ", LastLogin TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                    ", UNIQUE (Name))";
            s.execute(createTable);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to create table");
        } finally {
            closeConnection(c);
        }
    }

    public PlayerData getPlayerData(UUID playerID) {
        Connection c = null;
        try {
            c = connectionPool.getConnection();
            PreparedStatement s = c.prepareStatement("SELECT * FROM playerData WHERE UUID=?");
            s.setString(1, playerID.toString());
            ResultSet resultSet = s.executeQuery();
            if (resultSet.next()) {
                short groupId = resultSet.getShort("GROUPID");
                String name = resultSet.getString("NAME");
                boolean premium = resultSet.getBoolean("PREMIUM");
                String lastIp = resultSet.getString("LastIp");
                long lastLogin = resultSet.getLong("LastLogin");
                boolean tell = resultSet.getBoolean("TELL");
                PlayerData d = new PlayerData(name, playerID, groupId, premium, lastIp, lastLogin, tell);
                resultSet.close();
                s.close();
                return d;
            }
            resultSet.close();
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return getPlayerData(playerID);
        } finally {
            closeConnection(c);
        }
        return null;
    }

    public PlayerData getPlayerData(String name) {
        Connection c = null;
        try {
            c = connectionPool.getConnection();
            PreparedStatement s = c.prepareStatement("SELECT * FROM playerData WHERE LOWER(NAME)=?");
            s.setString(1, name.toLowerCase());
            ResultSet resultSet = s.executeQuery();
            if (resultSet.next()) {
                UUID id = UUID.fromString(resultSet.getString("UUID"));
                short groupId = resultSet.getShort("GROUPID");
                boolean premium = resultSet.getBoolean("PREMIUM");
                String lastIp = resultSet.getString("LastIp");
                long lastLogin = resultSet.getLong("LastLogin");
                boolean tell = resultSet.getBoolean("TELL");
                PlayerData d = new PlayerData(name, id, groupId, premium, lastIp, lastLogin, tell);
                resultSet.close();
                s.close();
                return d;
            }
            resultSet.close();
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return getPlayerData(name);
        } finally {
            closeConnection(c);
        }
        return null;
    }

    public void updatePlayer(UUID oldPlayerID, PlayerData d) {
        Connection c = null;
        try {
            c = connectionPool.getConnection();
            PreparedStatement update = c.prepareStatement("UPDATE playerData SET PREMIUM=? WHERE UUID=?");
            update.setBoolean(2, d.isPremium());
            update.setString(3, oldPlayerID.toString());
            update.executeUpdate();
            update.close();

            DatabaseMetaData metaData = c.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", null);
            while (tables.next()) {
                String tableName = tables.getString(3);
                PreparedStatement statement = c.prepareStatement("SELECT * FROM " + tableName);
                ResultSet resultSet = statement.executeQuery();
                ResultSetMetaData resultMeta = resultSet.getMetaData();
                for (int i = 0; i < resultMeta.getColumnCount(); i++) {
                    String columnName = resultMeta.getColumnName(i);
                    if (columnName.equalsIgnoreCase("UUID")) {
                        updateDataBase(c, columnName, oldPlayerID, d);
                        break;
                    }
                }
                resultSet.close();
            }
            tables.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(c);
        }
    }

    public void updateDataBase(Connection c, String tableName, UUID oldPlayerID, PlayerData d) {
        try {
            @Cleanup
            PreparedStatement update = c.prepareStatement("UPDATE ? SET UUID=? WHERE UUID=?");
            update.setString(1, tableName);
            update.setString(2, oldPlayerID.toString());
            update.setString(3, d.getUuid().toString());
            update.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void savePlayer(PlayerData d) {
        Connection c = null;
        try {
            c = connectionPool.getConnection();
            PreparedStatement select = c.prepareStatement("SELECT UUID FROM playerData WHERE UUID=?");
            select.setString(1, d.getUuid().toString());
            ResultSet resultSet = select.executeQuery();
            if (resultSet.next()) {
                PreparedStatement update = c.prepareStatement("UPDATE playerData SET NAME=?, LastIp=?,LastLogin=CURRENT_TIMESTAMP WHERE UUID=?");
                update.setString(1, d.getPlayerName());
                update.setString(2, d.getLastIp());

                update.setString(3, d.getUuid().toString());

                update.executeUpdate();
                //Check if any rows have been changed
                update.close();
                resultSet.close();
                select.close();
                return;
            }

            PreparedStatement save = c.prepareStatement("INSERT INTO playerData(UUID, NAME, GROUPID, PREMIUM, LastIp) values( ?, ?, ?, ?, ?)");
            save.setString(1, d.getUuid().toString());
            save.setString(2, d.getPlayerName());
            save.setInt(3, d.getGroupId());
            save.setBoolean(4, d.isPremium());
            save.setString(5, d.getLastIp());
            save.executeUpdate();
            save.close();
            resultSet.close();
            select.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(c);
        }
    }

    public UUID getPlayerID(String name) {
        Connection c = null;
        try {
            c = connectionPool.getConnection();
            name = name.toLowerCase();
            PreparedStatement s = c.prepareStatement("SELECT UUID FROM playerData WHERE LOWER(NAME)=?");
            s.setString(1, name);
            ResultSet resultSet = s.executeQuery();
            if (resultSet.next()) {
                UUID u = UUID.fromString(resultSet.getString("UUID"));
                resultSet.close();
                s.close();
                return u;
            }
            resultSet.close();
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return getPlayerID(name);
        } finally {
            closeConnection(c);
        }
        return null;
    }

    public void setGlobalGroupId(PlayerData pd, short groupId) {
        Connection c = null;
        try {
            c = connectionPool.getConnection();
            PreparedStatement update = c.prepareStatement("UPDATE playerData SET GROUPID=? WHERE UUID=?");
            update.setShort(1, groupId);
            update.setString(2, pd.getUuid().toString());
            update.executeUpdate();
            update.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(c);
        }
    }

    public List<PermissionsGroup> loadGroups() {

        List<PermissionsGroup> groups = new ArrayList<>();

        Connection c = null;
        try {
            c = connectionPool.getConnection();
            PreparedStatement select = c.prepareStatement("SELECT * FROM groupsConfig");
            ResultSet resultSet = select.executeQuery();
            while (resultSet.next()) {

                String servername = resultSet.getString("SERVER");
                String servertype = resultSet.getString("SERVERTYPE");
                short id = resultSet.getShort("ID");
                String display = ChatColor.translateAlternateColorCodes('&', resultSet.getString("DISPLAY"));
                String prefix = ChatColor.translateAlternateColorCodes('&', resultSet.getString("PREFIX"));
                String suffix = ChatColor.translateAlternateColorCodes('&', resultSet.getString("SUFFIX"));
                String perms = resultSet.getString("PERMISSIONS");
                boolean def = resultSet.getBoolean("ISDEFAULT");

                HashMap<String, Boolean> permissions = new HashMap<>();

                if (perms != null && perms.length() > 0) {
                    if (!perms.contains(", ")) {
                        List<String> perms1 = new ArrayList<>();
                        perms1.add(perms);
                        permissions = PermissionsGroupManager.getIns().loadPerms(perms1);
                    } else {
                        String[] ps = perms.split(", ");
                        List<String> perms1 = new ArrayList<>();
                        for (String p1 : ps)
                            perms1.add(p1);

                        permissions = PermissionsGroupManager.getIns().loadPerms(perms1);
                    }
                }
                PermissionsGroup pg = new PermissionsGroup(servername, servertype, id, def, display, prefix, suffix, permissions);
                groups.add(pg);
            }
            resultSet.close();
            select.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(c);
        }
        return groups;
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

}
