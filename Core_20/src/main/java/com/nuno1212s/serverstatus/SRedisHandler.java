package com.nuno1212s.serverstatus;

import com.nuno1212s.main.MainData;
import com.nuno1212s.util.Pair;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

public class SRedisHandler {

    /**
     * Update the player count of the current server
     *
     * @param playerCount The current player count
     */
    public void updatePlayerCount(Pair<Integer, Integer> playerCount) {
        try (Jedis redisConnection = MainData.getIns().getRedisHandler().getConnection()) {
            redisConnection.hset("ServerPlayerCount",
                    MainData.getIns().getServerManager().getServerName(),
                    String.valueOf(playerCount.key()) + "/" + String.valueOf(playerCount.value()));
        }
    }

    /**
     * Remove the player count for this server
     */
    public void removePlayerCount() {
        try (Jedis redisConnection = MainData.getIns().getRedisHandler().getConnection()) {
            redisConnection.hdel("ServerPlayerCount", MainData.getIns().getServerManager().getServerName());
        }
    }

    /**
     * Get the player counts for all the servers on the network
     *
     * @return A map that contains all the servers
     */
    public Map<String, Pair<Integer, Integer>> getPlayerCounts() {
        try (Jedis redisConnection = MainData.getIns().getRedisHandler().getConnection()) {
            Map<String, String> servers = redisConnection.hgetAll("ServerPlayerCount");
            Map<String, Pair<Integer, Integer>> serverPlayerCount = new HashMap<>();

            servers.forEach((serverName, playerCount) -> {
                String[] player = playerCount.split("/");
                serverPlayerCount.put(serverName, new Pair<>(Integer.parseInt(player[0]), Integer.parseInt(player[1])));
            });

            return serverPlayerCount;
        }
    }

}
