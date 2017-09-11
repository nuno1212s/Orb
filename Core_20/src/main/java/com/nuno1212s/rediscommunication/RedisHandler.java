package com.nuno1212s.rediscommunication;

import com.nuno1212s.config.Config;
import com.nuno1212s.main.MainData;
import lombok.Getter;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Base64;
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
        enabled = !(redis == null);

        if (enabled) {
            host = redis.getString("Host");
            port = redis.getInt("Port");
            password = redis.getString("Password");
            this.redisConnect();
        }
    }

    /**
     * Register a redis listener
     *
     * @param receiver
     */
    public void registerRedisListener(RedisReceiver receiver) {
        this.redisReceivers.add(receiver);
    }

    /**
     * Connect to the redis database, setup Sub pool
     */
    public void redisConnect() {
        //Must have 2 redis connections, 1 for SUB, 1 for PUB
        Jedis subConnection = new Jedis(host, port);
        redisConnection = new Jedis(host, port);

        if (!password.equalsIgnoreCase("")) {
            redisConnection.auth(password);
            subConnection.auth(password);
        }

        b = new RedisSubPub(subConnection);
        MainData.getIns().getScheduler().runTaskAsync(b);
    }

    /**
     * Send a message to the message pool
     *
     * @param message The byte data of the message {@link Message#toByteArray()}
     */
    public void sendMessage(byte[] message) {
        if (enabled) {

            String messageBuilder = MainData.getIns().getServerManager().getServerName() +
                    "||" + System.currentTimeMillis() +
                    "||" + Base64.getEncoder().encodeToString(message);

            MainData.getIns().getScheduler().runTaskAsync(() ->
                    redisConnection.publish("ServerData", messageBuilder)
            );
        }
    }

    /**
     * Close all the redis connections
     */
    public void close() {
        if (this.redisConnection != null) {
            this.redisConnection.close();
        }
        if (b != null) {
            b.getSubscriber().close();
        }
    }

}
