package com.nuno1212s.punishments.util;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.punishments.Punishment;
import com.nuno1212s.rediscommunication.Message;
import com.nuno1212s.util.Pair;
import com.nuno1212s.util.inventories.InventoryItem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.UUID;

public class PInventoryItem extends InventoryItem {

    @Getter
    private Punishment.PunishmentType type;

    @Getter
    private String reason;

    @Getter
    private String permission;

    @Getter
    private long durationInMillis;

    public PInventoryItem(JSONObject json) {
        super(json);
        this.permission = (String) json.getOrDefault("Permission", "punishments.all");
        this.type = Punishment.PunishmentType.valueOf((String) json.getOrDefault("PunishmentType", "MUTE"));
        this.reason = ChatColor.translateAlternateColorCodes('&', (String) json.getOrDefault("Reason", "Default Reason"));
        this.durationInMillis = (Long) json.getOrDefault("Duration",  0L) * 1000;
    }

    /**
     * Apply the punishment this item represents to a player
     * @param playerID
     */
    public void applyToPlayer(UUID playerID) {
        Punishment punishment = new Punishment(this.type, System.currentTimeMillis(), durationInMillis, this.reason);

        Pair<PlayerData, Boolean> playerData = MainData.getIns().getPlayerManager().getOrLoadPlayer(playerID);

        if (playerData.value()) {
            PlayerData player = playerData.key();

            player.setPunishment(punishment);
            player.save((o) -> {});

        } else {
            PlayerData player = playerData.key();
            player.setPunishment(punishment);

            Player p = Bukkit.getPlayer(player.getPlayerID());
            if (punishment.getPunishmentType() == Punishment.PunishmentType.BAN && !punishment.hasExpired()) {
                p.kickPlayer(reason);
            } else if (punishment.getPunishmentType() == Punishment.PunishmentType.MUTE && !punishment.hasExpired()) {
                MainData.getIns().getMessageManager().getMessage("Y_MUTED")
                        .format("%time%", punishment.timeToString()).sendTo(p);
            }
        }

        JSONObject data = new JSONObject();
        data.put("PLAYER", playerID.toString());
        data.put("PTYPE", type.name());
        data.put("REASON", reason);
        data.put("DURATION", durationInMillis);
        data.put("STARTING", punishment.getStartTime());
        Message msg = new Message("PUNISHMENT", "NEWPUNISHMENT", data);
        MainData.getIns().getRedisHandler().sendMessage(msg.toByteArray());

    }

}
