package com.nuno1212s.playermanager;

import com.google.common.cache.CacheBuilder;
import com.nuno1212s.main.Main;

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

}
