package com.nuno1212s.duels.matchmaking;

import com.nuno1212s.playermanager.PlayerData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MatchmakingManager {

    private Map<Integer, PriorityQueue<PlayerQueue>> queuedPlayers;

    private List<PendingMatchmaking> pendingMatchmakings;

    public MatchmakingManager() {

        queuedPlayers = new ConcurrentHashMap<>();

        pendingMatchmakings = new ArrayList<>();

    }

    /**
     * Tick the matchmaking
     */
    public void tick() {

        queuedPlayers.forEach((playerTime, playerQueues) -> {

            if (playerQueues.size() > 1) {

                createMatchMaking(playerQueues.peek(), playerQueues.peek());

            }
        });
    }

    /**
     * Creates a matchmaking for the two long lasting queues
     *
     * @param queue
     * @param queue2
     */
    void createMatchMaking(PlayerQueue queue, PlayerQueue queue2) {

        pendingMatchmakings.add(new PendingMatchmaking(queue, queue2));

    }

    /**
     * Handles a player leaving the party
     *
     * @param playerData The player that left the server
     */
    void handlePlayerLeaveParty(PlayerData playerData) {

        UUID playerID = playerData.getPlayerID();

        for (Map.Entry<Integer, PriorityQueue<PlayerQueue>> queues : this.queuedPlayers.entrySet()) {
            queues.getValue().removeIf(next -> next.getPlayerList().contains(playerID));
        }

        checkPendingMatchmaking(playerData);
    }

    private void checkPendingMatchmaking(PlayerData playerData) {

        Iterator<PendingMatchmaking> iterator = this.pendingMatchmakings.iterator();

        while (iterator.hasNext()) {

            PendingMatchmaking next = iterator.next();

            if (next.containsPlayer(playerData.getPlayerID())) {

                iterator.remove();

                next.notifyPlayerLeave(playerData);
            }

        }

    }

}
