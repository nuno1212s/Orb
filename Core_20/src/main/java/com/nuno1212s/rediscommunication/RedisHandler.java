package com.nuno1212s.rediscommunication;

import com.nuno1212s.config.Config;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import lombok.Getter;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles redis communication
 */
public class RedisHandler {

    private Jedis redisConnection;

    private String host, password;

    private int port;

    private RedisSubPub b;

    private boolean enabled;

    @Getter
    private List<RedisReceiver> redisReceivers;

    public RedisHandler(Config config) {

        redisReceivers = new ArrayList<>();
        Config redis = config.getConfigurationSection("Redis");
        enabled = redis != null && redis.getBoolean("Enabled");

        if (enabled) {
            host = redis.getString("Host");
            port = redis.getInt("Port");
            password = redis.getString("Password");
            this.redisConnect();
        }
    }

    public void registerRedisListener(RedisReceiver receiver) {
        this.redisReceivers.add(receiver);
    }

    public void redisConnect() {
        redisConnection = new Jedis(host, port);
        if (!password.equalsIgnoreCase("")){
            redisConnection.auth(password);
        }
        b = new RedisSubPub(redisConnection);
        MainData.getIns().getScheduler().runTaskAsync(b);
    }

    public void sendMessage(String message) {
        if (enabled) {
            redisConnection.publish("ServerData", message);
        }
    }

    public void close() {
        if (this.redisConnection != null) {
            this.redisConnection.close();
        }
        if (b != null) {
            b.getSubscribe().close();
        }
    }

}
