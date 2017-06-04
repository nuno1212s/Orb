package com.nuno1212s.playermanager;

import com.google.common.cache.CacheBuilder;
import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;
import com.nuno1212s.util.Pair;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Player manager
 */
public class PlayerManager {

    private final List<PlayerData> players;

    private Map<Object, Object> cache;

    public PlayerManager() {
        players = Collections.synchronizedList(new ArrayList<>());
        cache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build().asMap();
    }

    public PlayerData buildNewPlayerData(UUID playerID, String playerName) {
        return new CorePlayerData(playerID, new PlayerGroupData(), playerName, 0, System.currentTimeMillis(), true);
    }

    public PlayerData buildNewPiratePlayerData(UUID playerID, String playerName) {
        return new CorePlayerData(playerID, new PlayerGroupData(), playerName, 0, System.currentTimeMillis(), false);
    }

    public void addToCache(UUID player, PlayerData coreData) {
        this.cache.put(player, coreData);
    }

    public PlayerData getCachedPlayer(UUID player) {
        return (PlayerData) this.cache.get(player);
    }

    public void validatePlayerJoin(UUID player) {
        PlayerData playerInfo = (PlayerData) this.cache.get(player);
        this.cache.remove(player);
        this.players.add(playerInfo);
    }

    public void removePlayer(PlayerData d) {
        this.players.remove(d);
    }

    public PlayerData getPlayer(UUID playerID) {
        synchronized (players) {
            for (PlayerData player : players) {
                if (player.getPlayerID().equals(playerID)) {
                    return player;
                }
            }
        }
        return null;
    }

    public PlayerData getPlayer(String playerName) {
        synchronized (players) {
            for (PlayerData player : players) {
                if (player.getPlayerName().equals(playerName)) {
                    return player;
                }
            }
        }
        return null;
    }

    /**
     * Get or load the player data
     *
     * @param playerName The name of the player to load
     * @return The player data and if the data was loaded from the database (false if it is not loaded from the db, true if it is)
     */
    public Pair<PlayerData, Boolean> getOrLoadPlayer(String playerName) {
        synchronized (players) {
            for (PlayerData player : players) {
                if (player.getPlayerName().equalsIgnoreCase(playerName)) {
                    return new Pair<>(player, false);
                }
            }
        }

        return new Pair<>(MainData.getIns().getMySql().getPlayerData(playerName), true);
    }

    public Pair<PlayerData, Boolean> getOrLoadPlayer(UUID playerID) {
        synchronized (players) {
            for (PlayerData player : players) {
                if (player.getPlayerID().equals(playerID)) {
                    return new Pair<>(player, false);
                }
            }
        }

        return new Pair<>(MainData.getIns().getMySql().getPlayerData(playerID, null), true);
    }

    public List<PlayerData> getPlayers() {
        synchronized (players) {
            return new ArrayList<>(players);
        }
    }
}
