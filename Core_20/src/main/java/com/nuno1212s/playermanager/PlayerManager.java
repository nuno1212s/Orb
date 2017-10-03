package com.nuno1212s.playermanager;

import com.google.common.cache.CacheBuilder;
import com.nuno1212s.events.PlayerInformationLoadEvent;
import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;
import com.nuno1212s.util.Pair;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Player manager
 */
public class PlayerManager {

    private final Set<PlayerData> players;

    private Map<Object, Object> cache;

    public PlayerManager() {
        players = Collections.synchronizedSet(new HashSet<>());
        cache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build().asMap();
    }

    /**
     * Creates a new player data class for the given UUID and player name
     *
     * @param playerID The ID of the player
     * @param playerName The name of the player
     * @return
     */
    public PlayerData buildNewPlayerData(UUID playerID, String playerName) {
        return new CorePlayerData(playerID, new PlayerGroupData(), playerName, 0, System.currentTimeMillis(), true, MainData.getIns().getRewardManager().getDefaultRewards(), null);
    }

    /**
     * Creates a new player data class for the given UUID and player name.
     *
     * Note, this player class is marked as a hacked account (Not premium)
     *
     * @param playerID
     * @param playerName
     * @return
     */
    public PlayerData buildNewPiratePlayerData(UUID playerID, String playerName) {
        return new CorePlayerData(playerID, new PlayerGroupData(), playerName, 0, System.currentTimeMillis(), false, MainData.getIns().getRewardManager().getDefaultRewards(), null);
    }

    /**
     * Adds a player data to the player cache
     *
     * @param player The ID of the player
     * @param coreData The player data of the player
     */
    public void addToCache(UUID player, PlayerData coreData) {
        this.cache.put(player, coreData);
    }

    /**
     * Get the player data that is stored in the cache
     *
     * @param player
     * @return
     */
    public PlayerData getCachedPlayer(UUID player) {
        return (PlayerData) this.cache.get(player);
    }

    /**
     * Validates the player join
     *
     * @param player
     * @return
     */
    public PlayerData validatePlayerJoin(UUID player) {
        PlayerData playerInfo = (PlayerData) this.cache.get(player);
        this.cache.remove(player);
        this.players.add(playerInfo);
        return playerInfo;
    }

    /**
     * Removed a cached player from the cached players
     * @param player
     */
    public void removeCachedPlayer(UUID player) {
        this.cache.remove(player);
    }

    /**
     * Removes the player data from local storage
     * @param d
     */
    public void removePlayer(PlayerData d) {
        this.players.remove(d);
    }

    /**
     * Get the player data given the player UUID
     *
     * @param playerID
     * @return
     */
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

    /**
     * Get the player data given the player's name
     *
     * @param playerName
     * @return
     */
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
     * IMPORTANT: PLAYER CLASS LOADED HERE ONLY CONTAINS STANDARD DATA, NO PER SERVER INFO
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

    /**
     * Get or load the player data
     *
     * IMPORTANT: PLAYER CLASS LOADED HERE ONLY CONTAINS STANDARD DATA, NO PER SERVER INFO
     *
     * @param playerID The UUID of the player to load
     * @return The player data and if the data was loaded from the database (false if it is not loaded from the db, true if it is)
     */
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

    /**
     * Returns a safe to iterate player list
     *
     * NOTE: Any changes to this list (Removing / adding players will not cause a change in this classes player list)
     *
     * @return
     */
    public List<PlayerData> getPlayers() {
        synchronized (players) {
            return new ArrayList<>(players);
        }
    }
}
