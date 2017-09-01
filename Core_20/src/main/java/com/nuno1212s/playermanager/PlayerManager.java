package com.nuno1212s.playermanager;

import com.google.common.cache.CacheBuilder;
import com.nuno1212s.events.PlayerInformationLoadEvent;
import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.util.PlayerGroupData;
import com.nuno1212s.util.Pair;
import org.bukkit.Bukkit;

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
        return new CorePlayerData(playerID, new PlayerGroupData(), playerName, 0, System.currentTimeMillis(), true, MainData.getIns().getRewardManager().getDefaultRewards());
    }

    public PlayerData buildNewPiratePlayerData(UUID playerID, String playerName) {
        return new CorePlayerData(playerID, new PlayerGroupData(), playerName, 0, System.currentTimeMillis(), false, MainData.getIns().getRewardManager().getDefaultRewards());
    }

    public void addToCache(UUID player, PlayerData coreData) {
        this.cache.put(player, coreData);
    }

    public PlayerData getCachedPlayer(UUID player) {
        return (PlayerData) this.cache.get(player);
    }

    public PlayerData validatePlayerJoin(UUID player) {
        PlayerData playerInfo = (PlayerData) this.cache.get(player);
        this.cache.remove(player);
        this.players.add(playerInfo);
        return playerInfo;
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
     * IMPORTANT: PLAYER CLASS LOADED HERE ONLY CONTAINS STANDARD DATA, NO PER SERVER INFO ({@link #fullyLoadPlayer(String playerName)})
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
     * IMPORTANT: PLAYER CLASS LOADED HERE ONLY CONTAINS STANDARD DATA, NO PER SERVER INFO ({@link #fullyLoadPlayer(String playerName)})
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

    public PlayerData fullyLoadPlayer(String playerName) {
        PlayerData d = MainData.getIns().getMySql().getPlayerData(playerName);
        if (d == null) {
            return null;
        }

        PlayerInformationLoadEvent e = new PlayerInformationLoadEvent(d);
        Bukkit.getServer().getPluginManager().callEvent(e);

        return e.getPlayerInfo();
    }

    public List<PlayerData> getPlayers() {
        synchronized (players) {
            return new ArrayList<>(players);
        }
    }
}
