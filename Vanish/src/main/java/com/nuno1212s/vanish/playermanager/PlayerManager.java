package com.nuno1212s.vanish.playermanager;

import com.nuno1212s.vanish.main.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerManager {

    private List<UUID> vanishedPlayers;

    public PlayerManager() {
        vanishedPlayers = new ArrayList<>();
    }

    /**
     * Load the player into memory
     * @param playerID
     */
    public void loadPlayer(UUID playerID) {
        if (Main.getIns().getRedisHandler().isPlayerVanished(playerID)) {
            this.vanishedPlayers.add(playerID);
        }
    }

    /**
     * Check if the player is vanished
     * @param playerID
     * @return
     */
    public boolean isPlayerVanished(UUID playerID) {
        return this.vanishedPlayers.contains(playerID);
    }

    /**
     * Set a player vanished
     *
     * @param playerID
     * @param vanished
     */
    public void setPlayerVanished(UUID playerID, boolean vanished) {
        if (vanished) {
            this.vanishedPlayers.add(playerID);
        } else {
            this.vanishedPlayers.remove(playerID);
        }
    }

    /**
     * Unload the player from the memory
     * @param player
     */
    public void unloadPlayer(UUID player) {
        if (this.vanishedPlayers.contains(player)) {
            Main.getIns().getRedisHandler().setPlayerVanished(player, true);
            this.vanishedPlayers.remove(player);
        }
    }

}
