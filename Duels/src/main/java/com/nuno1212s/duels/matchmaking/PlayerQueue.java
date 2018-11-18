package com.nuno1212s.duels.matchmaking;

import com.nuno1212s.main.MainData;
import com.nuno1212s.messagemanager.Message;
import com.nuno1212s.playermanager.PlayerData;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerQueue implements Comparable<PlayerQueue> {

    @Getter
    private List<UUID> playerList;

    @Getter
    private long startQueue;

    public PlayerQueue() {
        playerList = new ArrayList<>();

        this.startQueue = System.currentTimeMillis();
    }

    public void notifyPlayerLeave(PlayerData data) {

        playerList.remove(data.getPlayerID());

        if (playerList.isEmpty()) {
            return;
        }

        Message player_left_queue = MainData.getIns().getMessageManager().getMessage("PLAYER_LEFT_QUEUE")
                .format("%playerName%", data.getNameWithPrefix());

        for (UUID uuid : playerList) {
            player_left_queue.sendTo(MainData.getIns().getPlayerManager().getPlayer(uuid));
        }
    }

    @Override
    public int compareTo(PlayerQueue playerQueue) {

        return Long.compare(this.startQueue, playerQueue.getStartQueue());

    }
}
