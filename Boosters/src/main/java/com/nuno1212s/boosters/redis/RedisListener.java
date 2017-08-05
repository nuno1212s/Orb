package com.nuno1212s.boosters.redis;

import com.nuno1212s.boosters.boosters.Booster;
import com.nuno1212s.rediscommunication.Message;
import com.nuno1212s.rediscommunication.RedisReceiver;

/**
 * Handles redis messages
 */
public class RedisListener implements RedisReceiver {

    public void handleBoosterActivation(Booster b) {

    }

    @Override
    public void onReceived(Message message) {

    }
}
