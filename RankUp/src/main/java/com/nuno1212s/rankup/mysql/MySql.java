package com.nuno1212s.rankup.mysql;

import com.google.common.collect.Sets;
import com.nuno1212s.enderchest.playerdata.EnderChestData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.rankup.main.Main;
import com.nuno1212s.rankup.playermanager.RUPlayerData;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * mysql data class
 */
public class MySql {

    public void createTables() {

        try (Connection c = MainData.getIns().getMySql().getConnection();
             Statement s = c.createStatement()) {

            String pvpData = "CREATE TABLE IF NOT EXISTS pvpData(UUID CHAR(40) PRIMARY KEY, COINS BIGINT, GROUPDATA VARCHAR(100)" +
                    ", SERVERGROUP VARCHAR(100), KITUSAGE VARCHAR(200), ENDERCHEST MEDIUMTEXT NOT NULL DEFAULT ''," +
                    "KILLS INTEGER, DEATHS INTEGER, INVITES VARCHAR(1000), CLANID CHAR(15))";

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

                    String enderChest = resultSet.getString("ENDERCHEST");

                    int kills = resultSet.getInt("KILLS"),
                            deaths = resultSet.getInt("DEATHS");

                    String invites = resultSet.getString("INVITES");

                    String clanID = resultSet.getString("CLANID");

                    Set<String> inv;

                    if (invites.equalsIgnoreCase("")) {
                        inv = new HashSet<>();
                    } else {
                        inv = Sets.newHashSet(invites.split(","));
                    }

                    return new RUPlayerData(playerData, coins, groupData, serverData, kitUsages, new ArrayList<>(), enderChest, kills, deaths, inv, clanID);
                }
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }

        short firstGroup = Main.getIns().getRankUpManager().getFirstGroup();

        return new RUPlayerData(playerData, 0, new PlayerGroupData(), new PlayerGroupData(firstGroup), new HashMap<>(), new ArrayList<>(), "", 0, 0, new HashSet<>(), null);
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

    public CompletableFuture<LinkedHashMap<UUID, Integer>> getKDDTop(int limit) {

        return CompletableFuture.supplyAsync(() -> {
            try (Connection c = MainData.getIns().getMySql().getConnection();
                 PreparedStatement st = c.prepareStatement("SELECT UUID, KILLS, DEATHS FROM pvpData ORDER BY (KILLS - DEATHS) LIMIT ?")) {

                st.setInt(1, limit);

                ResultSet resultSet = st.executeQuery();

                LinkedHashMap<UUID, Integer> top = new LinkedHashMap<>();

                while (resultSet.next()) {
                    UUID playerID = UUID.fromString(resultSet.getString("UUID"));

                    int KDD = resultSet.getInt("KILLS") - resultSet.getInt("DEATHS");

                    top.put(playerID, KDD);
                }

                resultSet.close();

                return top;
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return new LinkedHashMap<>();
        }, MainData.getIns().getAsyncExecutor());
    }

    public void savePlayerData(RUPlayerData playerData) {

        try (Connection c = MainData.getIns().getMySql().getConnection();
             PreparedStatement st = c.prepareStatement("INSERT INTO pvpData(UUID, COINS, GROUPDATA, SERVERGROUP, KITUSAGE, ENDERCHEST, KILLS, DEATHS, INVITES, CLANID) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE COINS=?, GROUPDATA=?, KITUSAGE=?, SERVERGROUP=?, ENDERCHEST=?, KILLS=?, DEATHS=?, INVITES=?, CLANID=?")) {

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

            String enderChestData = EnderChestData.inventoryToJSON(playerData.getEnderChest());

            st.setString(6, enderChestData);
            st.setInt(7, playerData.getKills());
            st.setInt(8, playerData.getDeaths());
            st.setString(9, playerData.invitesToString());
            st.setString(10, playerData.getClan());

            st.setLong(11, playerData.getCoins());
            st.setString(12, playerData.getGroupData().toDatabase());
            st.setString(13, kits);
            st.setString(14, playerData.getRankUpGroupData().toDatabase());
            st.setString(15, enderChestData);
            st.setInt(16, playerData.getKills());
            st.setInt(17, playerData.getDeaths());
            st.setString(18, playerData.invitesToString());
            st.setString(19, playerData.getClan());

            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
