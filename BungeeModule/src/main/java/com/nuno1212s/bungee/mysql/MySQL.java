package com.nuno1212s.bungee.mysql;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQL {

    public static void updateAutoLogin(PlayerData playerData) {

        try (Connection c = MainData.getIns().getMySql().getConnection();
             PreparedStatement st = c.prepareStatement("UPDATE playerData SET AUTOLOGIN=? WHERE UUID=?")) {

            st.setBoolean(1, playerData.isAutoLogin());
            st.setString(2, playerData.getPlayerID().toString());

            st.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
