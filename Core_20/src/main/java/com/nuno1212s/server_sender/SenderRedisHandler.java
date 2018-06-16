package com.nuno1212s.server_sender;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.rediscommunication.Message;
import com.nuno1212s.rediscommunication.RedisReceiver;
import org.json.simple.JSONObject;

import java.util.UUID;

public class SenderRedisHandler implements RedisReceiver {

    public SenderRedisHandler() {
        MainData.getIns().getRedisHandler().registerRedisListener(this);
    }

    @Override
    public String channel() {
        return "SERVER_SENDER";
    }

    public void sendPlayerTo(UUID p, String server) {
        JSONObject object = new JSONObject();
        object.put("PlayerID", p.toString());
        object.put("ServerName", server);

        Message m = new Message(channel(), "SEND_TO", object);
        MainData.getIns().getRedisHandler().sendMessage(m.toByteArray());
    }

    public void sendResponse(UUID p, boolean successful, String destinationServer, String message) {
        JSONObject object = new JSONObject();
        object.put("PlayerID", p.toString());
        object.put("Response", successful);
        object.put("Destination", destinationServer);
        object.put("Message", message);

        Message m = new Message(channel(), "SEND_RESPONSE", object);
        MainData.getIns().getRedisHandler().sendMessage(m.toByteArray());
    }

    @Override
    public void onReceived(Message message) {
        if (message.getChannel().equalsIgnoreCase(channel())) {
            if (message.getReason().equalsIgnoreCase("SEND_TO")) {
                if (!MainData.getIns().isBungee()) {
                    //This can only be handled by bungee
                    return;
                }

                JSONObject data = message.getData();

                UUID playerID = UUID.fromString((String) data.get("PlayerID"));
                String serverName = (String) data.get("ServerName");

                BungeeSender.getIns().send(playerID, serverName, message.getOGServer());

            } else if (message.getReason().equalsIgnoreCase("SEND_RESPONSE")) {
                //Check if the player was successfully connected to the server
                if (MainData.getIns().isBungee()) {
                    return;
                }


                JSONObject data = message.getData();

                UUID playerID = UUID.fromString((String) data.get("PlayerID"));
                String destination = (String) data.get("Destination");

                if (!destination.equalsIgnoreCase(MainData.getIns().getServerManager().getServerName())) {
                    return;
                }

                boolean success = (boolean) data.get("Response");

                BukkitSender.getIns().handleResponse(playerID, success, (String) data.get("Message"));

                if (!success) {
                    PlayerData player = MainData.getIns().getPlayerManager().getPlayer(playerID);

                    if (player == null) {
                        return;
                    }

                    player.setShouldSave(true);
                }

            }
        }
    }
}
