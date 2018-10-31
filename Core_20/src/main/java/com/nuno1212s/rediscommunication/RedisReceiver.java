package com.nuno1212s.rediscommunication;

import com.nuno1212s.main.MainData;

/**
 * Handles redis receiving
 */
public interface RedisReceiver {

    String channel();

    /**
     * Handles messages that are meant for this server (Messages that come from the own server are ignored)
     *
     * @param message
     */
    void onReceived(Message message);

}
