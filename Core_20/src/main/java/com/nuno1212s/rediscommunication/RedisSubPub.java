package com.nuno1212s.rediscommunication;

import com.nuno1212s.main.MainData;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * Redis sub pub pool
 */
public class RedisSubPub extends JedisPubSub implements Runnable {

    @Getter
    Jedis subscribe;

    public RedisSubPub(Jedis subscribe) {
        this.subscribe = subscribe;
    }


    @Override
    public void run() {
        subscribe.subscribe(this, "ServerData");
    }

    @Override
    public void onMessage(String channel, String message) {
        System.out.println("REDIS(" + MainData.getIns().getServerManager().getServerName() + ") - " + message);
        Message msg = new Message(message);
        MainData.getIns().getRedisHandler().getRedisReceivers().forEach(redisReceiver -> redisReceiver.onReceived(msg));
    }

}
