package com.nuno1212s.permissionmanager.util;

import com.nuno1212s.main.MainData;
import com.nuno1212s.rediscommunication.Message;
import com.nuno1212s.rediscommunication.RedisReceiver;
import org.json.simple.JSONObject;

public class PermissionRedisHandler implements RedisReceiver {

    public PermissionRedisHandler() {
        MainData.getIns().getRedisHandler().registerRedisListener(this);
    }

    @Override
    public String channel() {
        return "GROUPS";
    }

    public void publishGroupUpdate() {
        Message m = new Message(channel(), "UPDATEGROUPS", new JSONObject());

        MainData.getIns().getRedisHandler().sendMessage(m.toByteArray());
    }

    @Override
    public void onReceived(Message message) {
        if (message.getChannel().equalsIgnoreCase(channel())) {
            if (message.getReason().equalsIgnoreCase("UPDATEGROUPS")) {
                MainData.getIns().getPermissionManager().updateGroups();
            }
        }
    }
}
