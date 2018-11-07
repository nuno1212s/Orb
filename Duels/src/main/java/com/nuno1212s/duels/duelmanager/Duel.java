package com.nuno1212s.duels.duelmanager;

import java.util.UUID;

public class Duel {

    private UUID player1, player2;

    private UUID winner;

    private long date;

    public Duel(UUID player1, UUID player2) {
        this.player1 = player1;

        this.player2 = player2;
    }

    public void setWinner(UUID playerID) {
        this.winner = playerID;
    }

}
