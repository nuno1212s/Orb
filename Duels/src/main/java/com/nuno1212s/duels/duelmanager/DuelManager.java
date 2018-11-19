package com.nuno1212s.duels.duelmanager;

import com.google.common.collect.ImmutableList;
import com.nuno1212s.duels.DuelMain;
import com.nuno1212s.duels.arenas.Arena;
import com.nuno1212s.duels.matchmaking.PendingMatchmaking;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;

public class DuelManager {

    private PriorityQueue<Duel> duelQueue;

    private List<Duel> onGoingDuels;

    public DuelManager() {

        onGoingDuels = new ArrayList<>();

        duelQueue = new PriorityQueue<>();
    }

    /**
     * Get the active duel for a given player
     *
     * @param playerID
     * @return
     */
    public Duel getActiveDuelForPlayer(UUID playerID) {

        for (Duel onGoingDuel : onGoingDuels) {

            if (onGoingDuel.getTeam1().contains(playerID) || onGoingDuel.getTeam2().contains(playerID)) {
                return onGoingDuel;
            }

        }

        return null;
    }

    /**
     * Gets a immutable copy of the current on going duels
     *
     * @return
     */
    public List<Duel> getOnGoingDuels() {

        return ImmutableList.copyOf(this.onGoingDuels);

    }

    /**
     * Handles an arena becoming free
     *
     * @param arena
     */
    public void handleArenaFree(Arena arena) {

        if (!duelQueue.isEmpty()) {

            Duel firstDuel = duelQueue.peek();

            arena.fillArena(firstDuel);

            firstDuel.setArenaName(arena.getArenaName());

            onGoingDuels.add(firstDuel);
        }

    }

    public Duel startDuel(UUID player1, UUID player2) {

        Duel d = new Duel(player1, player2);

        //Teleport all the players to the spawns

        Arena clearArena = DuelMain.getIns().getArenaManager().getClearArena();

        checkAndFill(d, clearArena);

        return d;
    }

    public Duel startDuel(List<UUID> player1, List<UUID> player2) {

        if (player1.size() != player2.size()) {

            throw new IllegalArgumentException("Team sizes are different");

        }

        Duel d = new Duel(player1, player2);

        Arena clearArena = DuelMain.getIns().getArenaManager().getClearArena();

        checkAndFill(d, clearArena);

        return d;
    }

    public Duel startDuel(PendingMatchmaking matchmaking) {
        return startDuel(matchmaking.getQueue1().getPlayerList(), matchmaking.getQueue2().getPlayerList());
    }

    private void checkAndFill(Duel d, Arena clearArena) {
        if (clearArena == null) {

            duelQueue.add(d);

        } else {

            onGoingDuels.add(d);

            clearArena.fillArena(d);

            d.setArenaName(d.getArenaName());

        }
    }
}
