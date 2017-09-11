package com.nuno1212s.hub.redis;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.rediscommunication.Message;
import org.json.simple.JSONObject;

/**
 * Handle redis publishing
 */
public class RedisHandler {

    public void publishTellUpdate(PlayerData player) {
        JSONObject object = new JSONObject();
        object.put("PlayerID", player.getPlayerID().toString());
        object.put("Tell", player.isTell());

        Message message = new Message("BUNGEE", "TELLINFO", object);
        MainData.getIns().getRedisHandler().sendMessage(message.toByteArray());
    }

}
