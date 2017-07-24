package com.nuno1212s.npcinbox.database;

import com.nuno1212s.main.MainData;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles mysql transactions
 */
public class MySqlHandler {

    public MySqlHandler() {
        try (Connection c = MainData.getIns().getMySql().getConnection();
             Statement s = c.createStatement();) {
            String table = "CREATE TABLE IF NOT EXISTS npcInbox(ID INTEGER PRIMARY KEY AUTO_INCREMENT, SERVERTYPE varchar(25), REWARDTYPE varchar(10), REWARD TEXT)";
            s.execute(table);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
