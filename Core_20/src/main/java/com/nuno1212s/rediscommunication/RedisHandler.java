package com.nuno1212s.rediscommunication;

import com.nuno1212s.config.Config;
import com.nuno1212s.main.MainData;
import lombok.Getter;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Handles redis communication
 */
public class RedisHandler {

    @Getter
    private JedisPool redisPool;

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
     * Connect to the redis database, setup Sub redisPool
     */
    public void redisConnect() {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(15);

        this.redisPool = new JedisPool(poolConfig, host, port, 3000, password);


        //Must have 2 redis connections, 1 for SUB, 1 for PUB
        Jedis subConnection = this.redisPool.getResource();

        b = new RedisSubPub(subConnection);
        MainData.getIns().getScheduler().runTaskAsync(b);
    }

    /**
     * Send a message to the message redisPool
     *
     * @param message The byte data of the message {@link Message#toByteArray()}
     */
    public void sendMessage(byte[] message) {
        if (enabled) {

            String messageBuilder = MainData.getIns().getServerManager().getServerName() +
                    "||" + System.currentTimeMillis() +
                    "||" + Base64.getEncoder().encodeToString(message);

            MainData.getIns().getScheduler().runTaskAsync(() ->
                    getConnection().publish("ServerData", messageBuilder)
            );
        }
    }

    public Jedis getConnection() {
        return getRedisPool().getResource();
    }

    /**
     * Close all the redis connections
     */
    public void close() {
        redisPool.close();
    }

}
