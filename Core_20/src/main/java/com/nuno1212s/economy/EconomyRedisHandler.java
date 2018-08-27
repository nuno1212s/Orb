package com.nuno1212s.economy;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.rediscommunication.Message;
import com.nuno1212s.rediscommunication.RedisReceiver;
import com.nuno1212s.util.Pair;
import org.json.simple.JSONObject;

import java.util.UUID;
import java.util.function.Consumer;

public class EconomyRedisHandler implements RedisReceiver {

    public EconomyRedisHandler() {
        MainData.getIns().getRedisHandler().registerRedisListener(this);
    }

    @Override
    public String channel() {
        return "ECONOMY";
    }

    public void sendCashUpdate(UUID player, long cash, Operation cashOperation) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("UUID", player.toString());
        jsonObject.put("Operation", cashOperation.name());
        jsonObject.put("Cash", cash);
        Message m = new Message(channel(), "UPDATE_CASH", jsonObject);
        MainData.getIns().getRedisHandler().sendMessage(m.toByteArray());
    }

    @Override
    public void onReceived(Message message) {
        if (message.getChannel().equalsIgnoreCase(channel())) {
            if (message.getReason().equalsIgnoreCase("UPDATE_CASH")) {
                JSONObject data = message.getData();
                UUID playerID = UUID.fromString((String) data.get("UUID"));
                Operation op = Operation.valueOf((String) data.getOrDefault("Operation", "SET"));

                PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(playerID);

                if (playerData != null) {
                    op.applyTo(playerData, (Long) data.get("Cash"));
                }

            }
        }
    }

    public enum Operation {

        ADD(d -> d.getKey().addCash(d.getValue(), false)),
        REMOVE(d -> d.getKey().removeCash(d.getValue(), false)),
        SET(d -> d.getKey().setCash(d.getValue(), false));

        private Consumer<Pair<PlayerData, Long>> action;

        Operation(Consumer<Pair<PlayerData, Long>> callable) {
            this.action = callable;
        }

        public void applyTo(PlayerData d, long cash) {
            action.accept(new Pair<>(d, cash));
        }

    }
}
