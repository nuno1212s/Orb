package com.nuno1212s.clans.mysql;

import com.nuno1212s.clans.clanmanager.Clan;
import com.nuno1212s.main.MainData;

import java.sql.*;
import java.util.*;

public class MySQLHandler {

    public MySQLHandler() {

        createTables();

    }

    private void createTables() {

        try (Connection c = MainData.getIns().getMySql().getConnection();
             Statement s  = c.createStatement()) {

            s.execute("CREATE TABLE IF NOT EXISTS clans(CLANID char(15), OWNER char(40), MEMBERS varchar(3000)," +
                    " CLANTAG varchar(25), CLANNAME varchar(50), CLANDESC varchar(250), APPLICABLESERVER varchar(40)," +
                    "KILLS INTEGER, DEATHS INTEGER)");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<Clan> getClansForServer(String applicableServer) {

        try (Connection c = MainData.getIns().getMySql().getConnection();
             PreparedStatement s = c.prepareStatement("SELECT * FROM clans WHERE APPLICABLESERVER=?")) {

            s.setString(1, applicableServer);

            ResultSet resultSet = s.executeQuery();

            List<Clan> clans = new ArrayList<>();

            while (resultSet.next()) {
                clans.add(readClan(resultSet));
            }

            return clans;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Clan getClanByID(String clanID) {

        try (Connection c = MainData.getIns().getMySql().getConnection();
             PreparedStatement s = c.prepareStatement("SELECT * FROM clans WHERE CLANID=?")) {

            s.setString(1, clanID);

            ResultSet resultSet = s.executeQuery();

            if (resultSet.next()) {

                return readClan(resultSet);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     *
     *
     * @param clan
     */
    public void createClan(Clan clan) {

        try (Connection c = MainData.getIns().getMySql().getConnection();
            PreparedStatement s = c.prepareStatement("INSERT INTO clans(CLANID, OWNER, MEMBERS, CLANTAG, CLANNAME, CLANDESC, APPLICABLESERVER, KILLS, DEATHS) " +
                    "values(?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE MEMBERS=?, CLANTAG=?, CLANDESC =?, CLANNAME=?, KILLS=?, DEATHS=?")) {

            s.setString(1, clan.getClanID());
            s.setString(2, clan.getCreator().toString());
            s.setString(3, clan.membersToString());
            s.setString(4, clan.getClanTag());
            s.setString(5, clan.getClanName());
            s.setString(6, clan.getClanDescription());
            s.setString(7, clan.getApplicableServer());
            s.setInt(8, clan.getKills());
            s.setInt(9, clan.getDeaths());

            s.setString(10, clan.membersToString());
            s.setString(11, clan.getClanTag());
            s.setString(12, clan.getClanDescription());
            s.setString(13, clan.getClanName());
            s.setInt(14, clan.getKills());
            s.setInt(15, clan.getDeaths());

            s.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void removeClan(String clanID) {

        try (Connection c = MainData.getIns().getMySql().getConnection();
            PreparedStatement s = c.prepareStatement("DELETE FROM clans WHERE CLANID=?")) {

            s.setString(1, clanID);

            s.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private Clan readClan(ResultSet resultSet) throws SQLException {
        String clanID = resultSet.getString("CLANID");
        UUID owner = UUID.fromString(resultSet.getString("OWNER"));
        String[] membros = resultSet.getString("MEMBERS").split(",");

        Map<UUID, Clan.Rank> members = new HashMap<>(membros.length);

        for (String membro : membros) {
            members.put(UUID.fromString(membro.split(":")[0]), Clan.Rank.valueOf(membro.split(":")[1]));
        }

        String clanTag = resultSet.getString("CLANTAG");
        String clanName = resultSet.getString("CLANNAME");
        String clanDesc = resultSet.getString("CLANDESC");

        String applicableServer = resultSet.getString("APPLICABLESERVER");

        int kills = resultSet.getInt("KILLS");
        int deaths = resultSet.getInt("DEATHS");

        return new Clan(clanID, owner, members, clanName, clanTag, clanDesc, applicableServer, kills, deaths);
    }


}
