package com.nuno1212s.economy;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.rediscommunication.Message;
import com.nuno1212s.rediscommunication.RedisReceiver;
import org.json.simple.JSONObject;

import java.util.UUID;

public class EconomyRedisHandler implements RedisReceiver {

    public EconomyRedisHandler() {
        MainData.getIns().getRedisHandler().registerRedisListener(this);
    }

    @Override
    public String channel() {
        return "ECONOMY";
    }

    public void sendCashUpdate(UUID player, long cash) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("UUID", player.toString());
        jsonObject.put("Cash", cash);
        Message m = new Message(channel(), "UPDATE_CASH", jsonObject);
        MainData.getIns().getRedisHandler().sendMessage(m.toByteArray());
    }

    @Override
    public void onReceived(Message message) {
        if (message.getChannel().equalsIgnoreCase(channel())) {
            if (message.getReason().equalsIgnoreCase("UPDATE_CASH")) {
                JSONObject data = message.getData();
                UUID playerID = UUID.fromString( (String) data.get("UUID"));

                PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(playerID);

                if (playerData != null) {
                    playerData.setCash((Long) data.get("Cash"), false);
                }

            }
        }
    }
}
