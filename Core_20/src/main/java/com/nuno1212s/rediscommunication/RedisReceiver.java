package com.nuno1212s.rediscommunication;

/**
 * Handles redis receiving
 */
public interface RedisReceiver {

    void onReceived(Message message);

}
