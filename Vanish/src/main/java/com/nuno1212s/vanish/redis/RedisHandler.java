package com.nuno1212s.vanish.redis;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public class RedisHandler {

    /**
     * Check if the player is vanished
     *
     * @param playerID The ID of the player
     * @return
     */
    public boolean isPlayerVanished(UUID playerID) {
        Jedis redisConnection = MainData.getIns().getRedisHandler().getRedisConnection();

        String key = playerID.toString();

        if (redisConnection.exists(key)) {
            return Boolean.parseBoolean(redisConnection.get(key));
        }

        return false;
    }

    /**
     * Update the player vanished
     *
     * @param playerID
     * @param vanished
     */
    public void setPlayerVanished(UUID playerID, boolean vanished) {
        Jedis redisConnection = MainData.getIns().getRedisHandler().getRedisConnection();

        if (!vanished) {
            redisConnection.set(playerID.toString(), null);
        } else {
            redisConnection.set(playerID.toString(), String.valueOf(vanished));
        }

    }

}
