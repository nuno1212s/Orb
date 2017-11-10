package com.nuno1212s.permissionmanager.util;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * checks to see if the player's group has expired
 */
public class CheckExpirationTimer implements Runnable {

    @Override
    public void run() {
        List<PlayerData> players = MainData.getIns().getPlayerManager().getPlayers();
        players.forEach(playerData -> {
            playerData.checkExpiration(playerData);
        });
    }
}
