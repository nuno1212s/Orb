package com.nuno1212s.mercado.database;

import com.nuno1212s.main.MainData;
import com.nuno1212s.mercado.marketmanager.Item;
import com.nuno1212s.util.ItemUtils;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles MySQL
 */
public class MySql {

    //TODO

    public MySql() {
        try (Connection connection = MainData.getIns().getMySql().getConnection();
             Statement s = connection.createStatement()) {
            String createTable = "CREATE TABLE IF NOT EXISTS marketItems(ITEMID char(15) NOT NULL PRIMARY KEY, OWNER char(40)," +
                    "BUYER varchar(40), ITEM TEXT, COST BIGINT, PLACETIME BIGINT, SOLDTIME BIGINT," +
                    " SERVERCURRENCY BOOL, SOLD BOOL)";
            s.execute(createTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeItem(String itemID) {
        try (Connection connection = MainData.getIns().getMySql().getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM marketItems WHERE ITEMID=?")) {
            statement.setString(1, itemID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addItem(Item item) {
        try (Connection connection = MainData.getIns().getMySql().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO marketItems(ITEMID, OWNER, BUYER, ITEM, COST, PLACETIME, SOLDTIME, SERVERCURRENCY, SOLD) values(?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            statement.setString(1, item.getItemID());
            statement.setString(2, item.getOwner().toString());
            statement.setString(3, "");
            statement.setString(4, ItemUtils.itemTo64(item.getItem()));
            statement.setLong(5, item.getCost());
            statement.setLong(6, item.getPlaceTime());
            statement.setLong(7, System.currentTimeMillis());
            statement.setBoolean(8, item.isServerCurrency());
            statement.setBoolean(9, item.isSold());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateItem(Item item) {
        try (Connection connection = MainData.getIns().getMySql().getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE marketItems SET BUYER=?, SOLDTIME=?, SOLD=? WHERE ITEMID=?")) {
            statement.setString(1, item.getBuyer().toString());
            statement.setLong(2, item.getSoldTime());
            statement.setBoolean(3, item.isSold());
            statement.setString(4, item.getItemID());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        try (Connection connection = MainData.getIns().getMySql().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM marketItems")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String itemID = resultSet.getString("ITEMID");
                UUID owner = UUID.fromString(resultSet.getString("OWNER"));
                UUID buyer = resultSet.getString("BUYER").equalsIgnoreCase("") ? null : UUID.fromString(resultSet.getString("BUYER"));
                ItemStack item = ItemUtils.itemFrom64(resultSet.getString("ITEM"));
                long cost = resultSet.getLong("COST"), placeTime = resultSet.getLong("PLACETIME"), soldTime = resultSet.getLong("SOLDTIME");
                boolean serverCurrency = resultSet.getBoolean("SERVERCURRENCY"),
                        sold = resultSet.getBoolean("SOLD");
                Item i = new Item(itemID, owner, buyer, item, cost, placeTime, soldTime, serverCurrency, sold);
                items.add(i);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return items;
    }


}
