package com.nuno1212s.spawners.playerdata;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles player data
 */
public class PlayerManager {

    @Getter
    private List<PlayerData> players;

    public PlayerManager() {
        players = new ArrayList<>();

        new Timer();
    }

    public PlayerData getPlayer(UUID player) {
        for (PlayerData playerData : players) {
            if (playerData.getPlayerID().equals(player)) {
                return playerData;
            }
        }
        return null;
    }

    public PlayerData getPlayerInstance(UUID player, double effectiveModifier) {
        PlayerData playerData = new PlayerData(player, effectiveModifier);
        this.players.add(playerData);
        return playerData;
    }

    public void removePlayer(PlayerData d) {
        this.players.remove(d);
    }


}
