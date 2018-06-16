package com.nuno1212s.rediscommunication;

/**
 * Handles redis receiving
 */
public interface RedisReceiver {

    String channel();

    void onReceived(Message message);

}
