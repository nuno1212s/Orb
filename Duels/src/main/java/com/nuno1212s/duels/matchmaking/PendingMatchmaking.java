package com.nuno1212s.duels.matchmaking;

import com.nuno1212s.main.MainData;
import com.nuno1212s.messagemanager.Message;
import com.nuno1212s.playermanager.PlayerData;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PendingMatchmaking {

    @Getter
    PlayerQueue queue1, queue2;

    private long matchmakingTime;

    private Map<UUID, Boolean> accepted;

    public PendingMatchmaking(PlayerQueue queue1, PlayerQueue queue2) {

        this.queue1 = queue1;
        this.queue2 = queue2;

        this.matchmakingTime = System.currentTimeMillis();

        accepted = new HashMap<>();

    }

    private int calculateAccepted() {
        int playersAccepted = 0;

        for (Boolean value : this.accepted.values()) {
            if (value) {
                playersAccepted++;
            }
        }

        return playersAccepted;
    }

    private void notifyAccept() {

        int playersAccepted = calculateAccepted(), maxPlayers = queue1.getPlayerList().size() + queue2.getPlayerList().size();

        Message message = MainData.getIns().getMessageManager().getMessage("ACCEPTED_PLAYERS"),
                accept_message = MainData.getIns().getMessageManager().getMessage("ACCEPT_MATCHMAKING"),
                waiting_for_others = MainData.getIns().getMessageManager().getMessage("WAITING_FOR_OTHERS");

        char representChar = '#';

        StringBuilder builder = new StringBuilder();

        builder.append(ChatColor.GREEN.toString());

        for (int i = 0; i < playersAccepted; i++) {
            builder.append(representChar);
        }

        builder.append(ChatColor.RED.toString());

        for (int i = playersAccepted; i < maxPlayers; i++) {
            builder.append(representChar);
        }

        message.format("%playersAccepted%", builder.toString());

        sendMessage(message, accept_message, waiting_for_others, queue1);

        sendMessage(message, accept_message, waiting_for_others, queue2);
    }

    private void sendMessage(Message message, Message accept_message, Message waiting_for_others, PlayerQueue queue) {
        for (UUID uuid : queue.getPlayerList()) {

            PlayerData player = MainData.getIns().getPlayerManager().getPlayer(uuid);

            message.sendTo(player);

            if (!this.accepted.getOrDefault(uuid, false)) {

                accept_message.sendTo(player);

            } else {

                waiting_for_others.sendTo(player);

            }
        }
    }

    public boolean containsPlayer(UUID playerID) {

        return this.queue1.getPlayerList().contains(playerID) || this.queue2.getPlayerList().contains(playerID);

    }

    public void notifyPlayerLeave(PlayerData player) {

        if (this.queue1.getPlayerList().contains(player.getPlayerID()))  {

            queue1.notifyPlayerLeave(player);

        } else {

            queue2.notifyPlayerLeave(player);

        }

        Message player_left_pending = MainData.getIns().getMessageManager().getMessage("PLAYER_LEFT_PENDING");

        for (UUID uuid : this.queue1.getPlayerList()) {

            player_left_pending.sendTo(MainData.getIns().getPlayerManager().getPlayer(uuid));

        }

        for (UUID uuid : this.queue2.getPlayerList()) {

            player_left_pending.sendTo(MainData.getIns().getPlayerManager().getPlayer(uuid));

        }
    }


}
