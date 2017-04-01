package com.nuno1212s.core.mysql;

import com.nuno1212s.core.main.Main;
import com.nuno1212s.core.permissions.PermissionsAPI;
import com.nuno1212s.core.permissions.PermissionsGroup;
import com.nuno1212s.core.permissions.PermissionsGroupManager;
import com.nuno1212s.core.playermanager.PlayerData;
import com.nuno1212s.core.serverstatus.ServerInfo;
import com.nuno1212s.core.serverstatus.Status;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Handles MySql connections
 */
public class MySqlDB {

    private HikariDataSource dataSource;

    private String host, username, database;

    private char[] password;

    private int port;

    private Main m;

    private static MySqlDB ins;

    public static MySqlDB getIns() {
        return ins;
    }

    public MySqlDB(Main m) {
        ins = this;
        this.m = m;
        FileConfiguration config = this.m.getConfig();
        this.host = config.getString("Host");
        this.username = config.getString("Username");
        this.database = config.getString("Database");
        this.password = config.getString("Password").toCharArray();
        this.port = config.getInt("Port");
        openConnection();
    }

    private void openConnection() {
        HikariConfig cfg = new HikariConfig();
        cfg.setUsername(username);
        cfg.setPassword(new String(password));
        cfg.addDataSourceProperty("databaseName", database);
        cfg.setJdbcUrl("jdbc:mysql://" + host + ":" + String.valueOf(port) + "/" + database);
        cfg.setMaximumPoolSize(35);
        this.dataSource = new HikariDataSource(cfg);
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

    public PlayerData getPlayerData(UUID player, String name) {
        Connection c = null;
        try {
            c = getConnection();
            PreparedStatement get = c.prepareStatement("SELECT GROUPID, TELL, CHAT, CASH FROM playerData WHERE UUID=?");
            get.setString(1, player.toString());
            ResultSet resultSet = get.executeQuery();
            if (resultSet.next()) {
                PlayerData d = new PlayerData(player, name, resultSet.getShort("GROUPID"), resultSet.getBoolean("TELL"), resultSet.getBoolean("CHAT"), resultSet.getInt("CASH"));
                resultSet.close();
                get.close();
                return d;
            }
            resultSet.close();
            get.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(c);
        }
        return null;
    }

    public PlayerData getPlayerData(UUID player) {
        Connection c = null;
        try {
            c = getConnection();
            PreparedStatement get = c.prepareStatement("SELECT NAME,GROUPID, TELL, CHAT, CASH FROM playerData WHERE UUID=?");
            get.setString(1, player.toString());
            ResultSet resultSet = get.executeQuery();
            if (resultSet.next()) {
                PlayerData d = new PlayerData(player, resultSet.getString("NAME"), resultSet.getShort("GROUPID"), resultSet.getBoolean("TELL"), resultSet.getBoolean("CHAT"), resultSet.getInt("CASH"));
                resultSet.close();
                get.close();
                return d;
            }
            resultSet.close();
            get.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(c);
        }
        return null;
    }

    public PlayerData getPlayerData(String name) {
        Connection c = null;
        try {
            c = getConnection();
            PreparedStatement get = c.prepareStatement("SELECT UUID, GROUPID, TELL, CHAT, CASH FROM playerData WHERE LOWER(NAME)=?");
            get.setString(1, name.toLowerCase());
            ResultSet resultSet = get.executeQuery();
            if (resultSet.next()) {
                String uuid = resultSet.getString("UUID");
                PlayerData d = new PlayerData(UUID.fromString(uuid), name, resultSet.getShort("GROUPID"), resultSet.getBoolean("TELL"), resultSet.getBoolean("CHAT"), resultSet.getInt("CASH"));
                resultSet.close();
                get.close();
                return d;
            }
            resultSet.close();
            get.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(c);
        }
        return null;
    }

    public void updatePlayerData(PlayerData d) {
        Connection c = null;
        try {
            c = getConnection();
            PreparedStatement update = c.prepareStatement("UPDATE playerData SET TELL=?, CHAT=?, CASH=? WHERE UUID=?");
            update.setBoolean(1, d.isTell());
            update.setBoolean(2, d.isChat());
            update.setInt(3, d.getCash());
            update.setString(4, d.getId().toString());
            update.executeUpdate();
            update.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(c);
        }
    }

    public void changeGroup(UUID player, short group) {
        Connection c = null;
        try {
            c = getConnection();
            PreparedStatement update = c.prepareStatement("UPDATE playerData SET GROUPID=? WHERE UUID=?");
            update.setShort(1, group);
            update.setString(2, player.toString());
            update.executeUpdate();
            update.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(c);
        }
    }

    public void updateServerInformation(ServerInfo s) {
        Connection c = null;
        try {
            c = getConnection();
            PreparedStatement select = c.prepareStatement("SELECT SERVERNAME FROM serverInfo WHERE SERVERNAME=?");
            select.setString(1, s.getServerName());
            ResultSet resultSet = select.executeQuery();
            if (resultSet.next()) {
                PreparedStatement update = c.prepareStatement("UPDATE serverInfo SET CURRENTPLAYERS=?,MAXPLAYERS=?,STATUS=? WHERE SERVERNAME=?");
                update.setInt(1, s.getCurrentPlayers());
                update.setInt(2, s.getMaxPlayers());
                update.setString(3, s.getS().name());
                update.setString(4, s.getServerName());
                update.executeUpdate();
                update.close();
                resultSet.close();
                select.close();
                return;
            }
            PreparedStatement insert = c.prepareStatement("INSERT INTO serverInfo(SERVERNAME, CURRENTPLAYERS, MAXPLAYERS, STATUS) values(?, ?, ?, ?)");
            insert.setString(1, s.getServerName());
            insert.setInt(2, s.getCurrentPlayers());
            insert.setInt(3, s.getMaxPlayers());
            insert.setString(4, s.getS().name());
            insert.executeUpdate();
            insert.close();
            resultSet.close();
            select.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(c);
        }
    }

    public List<ServerInfo> getAllInfo() {
        Connection c = null;
        try {
            c = getConnection();
            PreparedStatement select = c.prepareStatement("SELECT * FROM serverInfo");
            ResultSet resultSet = select.executeQuery();
            List<ServerInfo> info = new ArrayList<>();
            while (resultSet.next()) {
                info.add(new ServerInfo(resultSet.getString("SERVERNAME"), resultSet.getInt("CURRENTPLAYERS"), resultSet.getInt("MAXPLAYERS"), Status.valueOf(resultSet.getString("STATUS"))));
            }
            resultSet.close();
            select.close();
            return info;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(c);
        }
        return null;
    }

    public ServerInfo getServerInformation(String serverName) {
        Connection c = null;
        try {
            c = getConnection();
            PreparedStatement select = c.prepareStatement("SELECT * FROM serverInfo WHERE SERVERNAME=?");
            select.setString(1, serverName);
            ResultSet resultSet = select.executeQuery();
            if (resultSet.next()) {
                ServerInfo serverInfo = new ServerInfo(serverName, resultSet.getInt("CURRENTPLAYERS"), resultSet.getInt("MAXPLAYERS"), Status.valueOf(resultSet.getString("STATUS")));
                resultSet.close();
                select.close();
                return serverInfo;
            }
            resultSet.close();
            select.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(c);
        }
        return null;
    }

    public PermissionsGroup getServerPlayerGroup(String tableName, UUID uuid) {
        PermissionsGroup pg = null;

        Connection c = null;
        try {
            c = getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT * FROM " + tableName + " WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                short groupid = resultSet.getShort("GROUPID");
                resultSet.close();
                ps.close();
                pg = PermissionsAPI.getIns().getGroup(groupid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(c);
        }
        if (pg == null)
            if (PermissionsGroupManager.getIns().getDefault() != null) {
                pg = PermissionsGroupManager.getIns().getDefault();
                setServerPlayerGroup(tableName, uuid, pg.getGroupId());
            }
        return pg;
    }

    public void setServerPlayerGroup(String tableName, UUID uuid, short groupid) {
        Connection c = null;
        try {
            c = getConnection();
            PreparedStatement update = c.prepareStatement("UPDATE " + tableName + " SET GROUPID=? WHERE UUID=?");
            update.setShort(1, groupid);
            update.setString(2, uuid.toString());
            update.executeUpdate();
            update.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(c);
        }
    }

    public void updateGroup(PermissionsGroup pg) {
        String perms = "";
        HashMap<String, Boolean> pers = pg.getPermissions();
        int a = 0;
        for (String pe : pers.keySet()) {
            String c = ", ";
            if (a == 0)
                c = "";
            if (pers.get(pe) == false)
                perms = pe + c + "-" + perms;
            else
                perms = pe + c + perms;
            a++;
        }
        Connection c = null;
        try {
            c = getConnection();
            PreparedStatement select = c.prepareStatement("SELECT ID FROM groupsConfig WHERE ID=?");
            select.setShort(1, pg.getGroupId());
            ResultSet resultSet = select.executeQuery();
            if (resultSet.next()) {
                PreparedStatement update = c.prepareStatement("UPDATE groupsConfig SET SERVERTYPE=?,SERVER=?,DISPLAY=?,PREFIX=?,SUFFIX=?,PERMISSIONS=?,ISDEFAULT=?,scoreboardDisplay=? WHERE ID=?");
                update.setString(1, pg.getServerType());
                update.setString(2, pg.getServerName());
                update.setString(3, pg.getDisplay());
                update.setString(4, pg.getPrefix());
                update.setString(5, pg.getSuffix());
                update.setString(6, perms);
                update.setBoolean(7, pg.isDefault());
                update.setString(8, pg.getScoreboardName());
                update.setShort(9, pg.getGroupId());
                update.executeUpdate();
                update.close();
                resultSet.close();
                select.close();
                return;
            }
            PreparedStatement insert = c.prepareStatement("INSERT INTO groupsConfig(ID, SERVERTYPE, SERVER, DISPLAY, PREFIX, SUFFIX, PERMISSIONS, ISDEFAULT, scoreboardDisplay) values(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            insert.setShort(1, pg.getGroupId());
            insert.setString(2, pg.getServerType());
            insert.setString(3, pg.getServerName());
            insert.setString(4, pg.getDisplay());
            insert.setString(5, pg.getPrefix());
            insert.setString(6, pg.getSuffix());
            insert.setString(7, perms);
            insert.setBoolean(8, pg.isDefault());
            insert.setString(9, pg.getScoreboardName());
            insert.executeUpdate();
            insert.close();
            resultSet.close();
            select.close();
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
            c = getConnection();
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
                boolean override = resultSet.getBoolean("IMPLEMENTS_ALL_SERVERS");
                String scoreboardName = resultSet.getString("scoreboardDisplay");

                HashMap<String, Boolean> permissions = new HashMap<>();

                if (perms != null && perms.length() > 0) {
                    String[] ps = perms.split(", ");
                    List<String> perms1 = new ArrayList<>();
                    for (String p1 : ps)
                        perms1.add(p1);

                    permissions = PermissionsGroupManager.getIns().loadPerms(perms1);

                }
                PermissionsGroup pg = new PermissionsGroup(servername, servertype, id, def, display, prefix, suffix, permissions, override, scoreboardName);
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

}
