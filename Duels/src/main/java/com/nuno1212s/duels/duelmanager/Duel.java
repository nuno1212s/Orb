package com.nuno1212s.duels.duelmanager;

import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Duel {

    @Getter
    private List<UUID> team1, team2;

    private List<UUID> winners;

    private long dateStart;

    public Duel(UUID player1, UUID player2) {
        this.team1 = Collections.singletonList(player1);

        this.team2 = Collections.singletonList(player2);

        this.dateStart = System.currentTimeMillis();
    }

    /**
     * Set the winner of the duel to the team
     * @param team
     */
    public void setWinner(List<UUID> team) {
        this.winners = team;
    }

}
