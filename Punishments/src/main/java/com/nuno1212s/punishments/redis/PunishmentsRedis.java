package com.nuno1212s.punishments.redis;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.punishments.Punishment;
import com.nuno1212s.rediscommunication.Message;
import com.nuno1212s.rediscommunication.RedisReceiver;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;

import java.util.UUID;

public class PunishmentsRedis implements RedisReceiver {

    @Override
    public String channel() {
        return "PUNISHMENT";
    }

    @Override
    public void onReceived(Message message) {
        System.out.println(Bukkit.isPrimaryThread());
        if (message.getChannel().equalsIgnoreCase(channel())) {
            if (message.getReason().equalsIgnoreCase("NEWPUNISHMENT")) {
                JSONObject data = message.getData();
                UUID player = UUID.fromString((String) data.get("PLAYER"));
                Punishment.PunishmentType type = Punishment.PunishmentType.valueOf((String) data.get("PTYPE"));
                String reason = (String) data.get("REASON");
                long durationInMillis = (Long) data.get("DURATION"), startingTime = (Long) data.get("STARTING");

                PlayerData d = MainData.getIns().getPlayerManager().getPlayer(player);
                Punishment p = new Punishment(type, startingTime, durationInMillis, reason);

                d.setPunishment(p);
                System.out.println(Bukkit.isPrimaryThread());
            }
        }
    }
}
