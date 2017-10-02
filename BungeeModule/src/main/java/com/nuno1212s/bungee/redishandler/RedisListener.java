package com.nuno1212s.bungee.redishandler;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.rediscommunication.Message;
import com.nuno1212s.rediscommunication.RedisReceiver;
import org.json.simple.JSONObject;

import java.util.UUID;

/**
 * Handles redis messages
 */
public class RedisListener implements RedisReceiver {

    @Override
    public String channel() {
        return "BUNGEE";
    }

    @Override
    public void onReceived(Message message) {
        if (!message.getChannel().equalsIgnoreCase(channel())) {
            return;
        }

        if (message.getReason().equalsIgnoreCase("TELLINFO")) {
            JSONObject data = message.getData();

            UUID playerID = UUID.fromString((String) data.getOrDefault("PlayerID", ""));
            boolean newValue = (Boolean) data.getOrDefault("Tell", false);

            PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(playerID);

            if (playerData == null) {
                return;
            }

            playerData.setTell(newValue);

        } else if (message.getReason().equalsIgnoreCase("GROUPUPDATE")) {
            JSONObject data = message.getData();

            UUID playerID = UUID.fromString((String) data.getOrDefault("PlayerID", ""));
            short newGroupID = ((Long) data.getOrDefault("GroupID", 1)).shortValue();
            long duration = ((Long) data.getOrDefault("Duration", -1));

            PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(playerID);

            if (playerData == null) {
                return;
            }

            playerData.setMainGroup(newGroupID, duration, false);
        }

    }
}
