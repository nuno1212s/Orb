package com.nuno1212s.server_sender;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.Callback;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Sends bungee information
 */
public class BukkitSender {

    BukkitMain m;

    @Getter
    static BukkitSender ins;

    private List<UUID> waitingForResponse;

    public BukkitSender(BukkitMain ms) {
        ins = this;
        waitingForResponse = new ArrayList<>();
        m = ms;
    }

    public void handleResponse(UUID player, boolean success, String reason) {
        this.waitingForResponse.remove(player);

        if (!success) {
            PlayerData player1 = MainData.getIns().getPlayerManager().getPlayer(player);

            if (player1 == null) {
                return;
            }

            MainData.getIns().getMessageManager().getMessage("FAILED_TO_JOIN")
                    .format("%reason%", reason).sendTo(player1);
        }
    }

    public void sendPlayer(PlayerData data, Player p, String bungeeServer) {
        if (waitingForResponse.contains(p.getUniqueId())) {
            MainData.getIns().getMessageManager().getMessage("WAITING_FOR_RESPONSE").sendTo(p);
            return;
        }

        Callback c = (o) -> {
            MainData.getIns().getServerManager().getSenderRedisHandler().sendPlayerTo(p.getUniqueId(), bungeeServer);
        };

        data.setShouldSave(false);
        waitingForResponse.add(p.getUniqueId());
        data.save(c);

    }

}
