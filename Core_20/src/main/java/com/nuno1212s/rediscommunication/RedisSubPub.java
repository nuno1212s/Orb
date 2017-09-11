package com.nuno1212s.rediscommunication;

import com.nuno1212s.main.MainData;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.Base64;

/**
 * Redis sub pub pool
 */
public class RedisSubPub extends JedisPubSub implements Runnable {

    @Getter
    Jedis subscriber;

    public RedisSubPub(Jedis subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void run() {
        subscriber.subscribe(this, "ServerData");
    }

    @Override
    public void onMessage(String channel, String message) {

        String[] split = message.split("\\|\\|");

        String originalServer = split[0];

        long timeSent = Long.parseLong(split[1]);

        Message msg = new Message(Base64.getDecoder().decode(split[2]));

        //Still print out info, for debug
        System.out.println("REDIS(" + MainData.getIns().getServerManager().getServerName() + ") - ");
        System.out.println("OG SERVER: " + originalServer);
        System.out.println("TIME SENT: " + timeSent + " Took " + (System.currentTimeMillis() - timeSent) + " ms");
        System.out.println("MESSAGE (Base 64): " + split[2]);

        if (originalServer.equalsIgnoreCase(MainData.getIns().getServerManager().getServerName())) {
            //Redis message originated here...
            return;
        }

        if (!originalServer.equalsIgnoreCase(MainData.getIns().getServerManager().getServerName())) {
            MainData.getIns().getRedisHandler().getRedisReceivers().forEach(redisReceiver -> redisReceiver.onReceived(msg));
        }


    }

}
