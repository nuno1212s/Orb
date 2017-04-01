package com.nuno1212s.playermanager;

import com.google.common.cache.CacheBuilder;
import com.nuno1212s.main.Main;
import com.nuno1212s.mysql.MySqlHandler;
import net.md_5.bungee.api.ProxyServer;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;


/**
 * Manages player data
 */
public class PlayerManager {

    public HashMap<String, String> lastTell = new HashMap<String, String>();

    private final Map<UUID, PlayerData> players = Collections.synchronizedMap(new HashMap<>());

    private final ConcurrentMap<Object, Object> player = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build().asMap();

    static PlayerManager ins = new PlayerManager();

    public static PlayerManager getIns() {
        return ins;
    }

    public void addTemporaryPlayer(PlayerData d) {
        player.put(d.getUuid(), d);
    }

    public PlayerData getTempData(UUID u) {
        return (PlayerData) player.get(u);
    }

    public void removeTempData(UUID u) {
        player.remove(u);
    }

    public PlayerData getPlayer(UUID player) {
        synchronized (players) {
            if (players.containsKey(player)) {
                return players.get(player);
            }
        }
        return null;
    }

    public PlayerData getPlayer(String name) {
        synchronized (players) {
            for (PlayerData playerData : players.values()) {
                if (playerData.getName().equalsIgnoreCase(name)) {
                    return playerData;
                }
            }
        }
        return null;
    }

    public UUID getPlayerID(String name) {
        return MySqlHandler.getIns().getPlayerID(name);
    }

    public void addPlayer(PlayerData d) {
        this.players.put(d.getUuid(), d);
    }

    public void removePlayer(PlayerData d) {
        this.players.remove(d.getUuid());
    }

    public List<PlayerData> getOnlinePlayers() {
        return new ArrayList<>(players.values());
    }

    public void setGroupId(PlayerData pd, short groupId) {
        ProxyServer.getInstance().getScheduler().runAsync(Main.getInstance(), () -> {
                    MySqlHandler.getIns().setGlobalGroupId(pd, groupId);
                    pd.setGroupId(groupId);
                }
        );
    }

}
