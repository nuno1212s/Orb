package com.nuno1212s.boosters.mysql;

import com.nuno1212s.boosters.boosters.Booster;
import com.nuno1212s.boosters.boosters.BoosterType;
import com.nuno1212s.main.MainData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * MySQl handle
 */
public class MySql {

    public MySql() {
        createTables();
    }

    private void createTables() {
        try (Connection c = MainData.getIns().getMySql().getConnection();
             Statement statement = c.createStatement();) {
            String s = "CREATE TABLE IF NOT EXISTS boosters(ID char(5) PRIMARY KEY, OWNER char(40), BOOSTERTYPE varchar(13), MULTIPLIER FLOAT, DURATIONINMILLIS BIGINT, ACTIVATIONTIME BIGINT, ACTIVATED BOOL, APPLICABLESERVER varchar(25), CUSTOMNAME varchar(30))";
            statement.execute(s);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void removeBooster(String boosterID) {
        try (Connection c = MainData.getIns().getMySql().getConnection();
             PreparedStatement s = c.prepareStatement("DELETE FROM boosters WHERE ID=?")) {
            s.setString(1, boosterID);
            s.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveBooster(Booster b) {
        try (Connection c = MainData.getIns().getMySql().getConnection();
             PreparedStatement s = c.prepareStatement("INSERT INTO boosters values(?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            s.setString(1, b.getBoosterID());
            s.setString(2, b.getOwner() == null ? "" : b.getOwner().toString());
            s.setString(3, b.getType().name());
            s.setFloat(4, b.getMultiplier());
            s.setLong(5, b.getDurationInMillis());
            s.setLong(6, b.getActivationTime());
            s.setBoolean(7, b.isActivated());
            s.setString(8, b.getApplicableServer());
            s.setString(9, b.getCustomName());
            s.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Booster> loadBoosters() {
        ArrayList<Booster> loadBoosters = new ArrayList<>();

        try (Connection c = MainData.getIns().getMySql().getConnection();
             PreparedStatement s = c.prepareStatement("SELECT * FROM boosters")) {

            ResultSet resultSet = s.executeQuery();

            while (resultSet.next()) {

                String owner1 = resultSet.getString("OWNER");
                UUID owner = owner1.equalsIgnoreCase("") ? null : UUID.fromString(owner1);
                String boosterID = resultSet.getString("ID");
                BoosterType type = BoosterType.valueOf(resultSet.getString("BOOSTERTYPE"));
                float multiplier = resultSet.getFloat("MULTIPLIER");
                long duration = resultSet.getLong("DURATIONINMILLIS"), activation = resultSet.getLong("ACTIVATIONTIME");
                boolean activated = resultSet.getBoolean("ACTIVATED");
                String applicableServer = resultSet.getString("APPLICABLESERVER");
                String customName = resultSet.getString("CUSTOMNAME");

                loadBoosters.add(new Booster(boosterID, owner, type, multiplier, duration, activation, activated, applicableServer, customName));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return loadBoosters;
    }

    public List<Booster> loadBoosters(UUID ownerID) {
        ArrayList<Booster> booster = new ArrayList<>();

        try (Connection c = MainData.getIns().getMySql().getConnection();
            PreparedStatement s = c.prepareStatement("SELECT * FROM boosters WHERE OWNER=?")) {

            s.setString(1, ownerID.toString());

            ResultSet resultSet = s.executeQuery();

            while(resultSet.next()) {
                String boosterID = resultSet.getString("ID");
                BoosterType type = BoosterType.valueOf(resultSet.getString("BOOSTERTYPE"));
                float multiplier = resultSet.getFloat("MULTIPLIER");
                long duration = resultSet.getLong("DURATIONINMILLIS"), activation = resultSet.getLong("ACTIVATIONTIME");
                boolean activated = resultSet.getBoolean("ACTIVATED");
                String applicableServer = resultSet.getString("APPLICABLESERVER");
                String customName = resultSet.getString("CUSTOMNAME");

                booster.add(new Booster(boosterID, ownerID, type, multiplier, duration, activation, activated, applicableServer, customName));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return booster;

    }

}
