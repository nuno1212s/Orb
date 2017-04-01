package com.nuno1212s.core.playermanager;

import com.nuno1212s.core.main.Main;
import com.nuno1212s.core.mysql.MySqlDB;

import java.util.*;

/**
 * Handles the player data
 */
public class PlayerManager {

    private final Map<UUID, PlayerData> players;

    private PlayerManaging serverManaging;

    static PlayerManager ins;

    public static PlayerManager getIns() {
        return ins;
    }

    private Main m;

    Main getM() {
        return m;
    }

    public PlayerManager(Main m) {
        ins = this;
        this.m = m;
        players = Collections.synchronizedMap(new HashMap<>());
    }

    public List<PlayerData> getPlayers() {
        return new ArrayList<>(this.players.values());
    }

    public void setServerManager(PlayerManaging serverManager) {
        serverManaging = serverManager;
    }
    
    public PlayerData getPlayerID(String playerName) {
    	synchronized (players) {
            for (PlayerData playerData : players.values()) {
                if (playerData.getName().equalsIgnoreCase(playerName)) {
                    return playerData;
                }
            }
        }
        return MySqlDB.getIns().getPlayerData(playerName);
    }

    public PlayerData getPlayerData(UUID player) {
        synchronized (players) {
            if (players.containsKey(player)) {
                return players.get(player);
            }
        }
        return null;
    }

    public PlayerData requestPlayerData(UUID player) {
        PlayerData playerData = getPlayerData(player);
        return playerData == null ? MySqlDB.getIns().getPlayerData(player) : playerData;
    }

    public void addPlayer(PlayerData d) {
        players.put(d.getId(), d);
    }

    public void removePlayer(PlayerData d) {
        players.remove(d.getId());
    }

}
