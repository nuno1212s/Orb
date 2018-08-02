package com.nuno1212s.rediscommunication;

import com.nuno1212s.main.MainData;

/**
 * Handles redis receiving
 */
public interface RedisReceiver {

    String channel();

    void onReceived(Message message);

}
