package com.nuno1212s.duels.matchmaking;

import com.nuno1212s.duels.DuelMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.party.partymanager.Party;
import com.nuno1212s.playermanager.PlayerData;

import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MatchmakingManager {

    private Map<Integer, PriorityQueue<PlayerQueue>> queuedPlayers;

    private PriorityQueue<PendingMatchmaking> pendingMatchmakings;

    public MatchmakingManager() {

        queuedPlayers = new ConcurrentHashMap<>();

        pendingMatchmakings = new PriorityQueue<>();

        MainData.getIns().getScheduler().runTaskTimer(this::tick, 100, 20);

    }

    /**
     * Add the players to the queuePlayers list
     *
     * @param party
     */
    public void addPlayersToQueue(Party party) {

        PlayerQueue playerQueue = new PlayerQueue(party);

        PriorityQueue<PlayerQueue> pQ = this.queuedPlayers.getOrDefault(playerQueue.getPlayerList().size(), new PriorityQueue<>());

        pQ.add(playerQueue);

        queuedPlayers.put(party.getMembers().size(), pQ);
    }

    /**
     * Tick the matchmaking
     */
    public void tick() {

        queuedPlayers.forEach((playerTime, playerQueues) -> {

            if (playerQueues.size() > 1) {

                createMatchMaking(playerQueues.poll(), playerQueues.poll());

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

    public void removePendingMatchmaking(PendingMatchmaking pendingMatchmaking)  {
        this.pendingMatchmakings.remove(pendingMatchmaking);
    }

    void handleArenaClearEvent() {
        if (!pendingMatchmakings.isEmpty())
            if (pendingMatchmakings.peek().isReady()) {
                DuelMain.getIns().getDuelManager().startDuel(pendingMatchmakings.poll());
            }
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
