package com.nuno1212s.boosters.redis;

import com.nuno1212s.boosters.boosters.Booster;
import com.nuno1212s.rediscommunication.Message;
import com.nuno1212s.rediscommunication.RedisReceiver;

/**
 * Handles redis messages
 */
public class RedisListener implements RedisReceiver {

    @Override
    public String channel() {
        return "BOOSTERS";
    }

    public void handleBoosterActivation(Booster b) {

    }

    public void addBooster(Booster b) {

    }

    public void handleBoosterDeletion(Booster b) {

    }

    @Override
    public void onReceived(Message message) {

    }
}
