package com.nuno1212s.boosters.redis;

import com.nuno1212s.boosters.boosters.Booster;
import com.nuno1212s.boosters.boosters.BoosterType;
import com.nuno1212s.boosters.main.Main;
import com.nuno1212s.main.MainData;
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
        return "BOOSTERS";
    }


    @Override
    public void onReceived(Message message) {
        if (!message.getChannel().equalsIgnoreCase(channel())) {
            return;
        }

        if (message.getReason().equalsIgnoreCase("BOOSTER_ACTIVATE")) {

            Booster b = Main.getIns().getBoosterManager().getBooster((String) message.getData().get("BOOSTER"));

            if (b != null) {
                b.activate((Long) message.getData().get("ACTIVATIONTIME"), false);
            }

        } else if (message.getReason().equalsIgnoreCase("BOOSTER_ADD")) {

            JSONObject data = message.getData();

            Booster b = new Booster((String) data.get("BOOSTER_ID"),
                    UUID.fromString((String) data.get("OWNER")),
                    BoosterType.valueOf((String) data.get("BOOSTER_TYPE")),
                    ((Double) data.get("MULTIPLIER")).floatValue(),
                    (Long) data.get("DURATION"),
                    0,
                    false,
                    (String) data.get("APPLICABLESERVER"),
                    (String) data.get("CUSTOMNAME"));

            Main.getIns().getBoosterManager().addBooster(b, false);

        } else if (message.getReason().equalsIgnoreCase("BOOSTER_REMOVE")) {

            Booster boosterid = Main.getIns().getBoosterManager().getBooster((String) message.getData().get("BOOSTERID"));

            if (boosterid != null) {
                Main.getIns().getBoosterManager().handleBoosterExpiration(boosterid, false);
            }

        }
    }

    public void handleBoosterActivation(Booster b) {
        MainData.getIns().getRedisHandler().sendMessage(
                new Message(channel())
                        .setReason("BOOSTER_ACTIVATE")
                        .add("BOOSTER", b.getBoosterID())
                        .add("ACTIVATIONTIME", b.getActivationTime())
                        .toByteArray());
    }

    public void addBooster(Booster b) {
        MainData.getIns().getRedisHandler().sendMessage(
                new Message(channel())
                        .setReason("BOOSTER_ADD")
                        .add("BOOSTER_ID", b.getBoosterID())
                        .add("OWNER", b.getOwner())
                        .add("BOOSTER_TYPE", b.getType().name())
                        .add("MUTIPLIER", b.getMultiplier())
                        .add("DURATION", b.getDurationInMillis())
                        .add("APPLICABLESERVER", b.getApplicableServer())
                        .add("CUSTOMNAME", b.getCustomName())
                        .toByteArray());
    }

    public void handleBoosterDeletion(Booster b) {
        MainData.getIns().getRedisHandler().sendMessage(
                new Message(channel())
                        .setReason("BOOSTER_REMOVE")
                        .add("BOOSTERID", b.getBoosterID())
                        .toByteArray());
    }
}
