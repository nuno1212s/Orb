package com.nuno1212s.duels.duelmanager;

import com.nuno1212s.main.MainData;
import com.nuno1212s.messagemanager.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Duel implements Comparable<Duel> {

    public transient static final long WARMUP_TIME = TimeUnit.SECONDS.toMillis(15);

    private transient static final List<Integer> toNotify = Arrays.asList(15, 10, 5, 4, 3, 2, 1);

    private transient List<Integer> notifiedTimes = new ArrayList<>(toNotify.size());

    private transient List<UUID> spectators = new ArrayList<>();

    @Getter
    private List<UUID> team1, team2;

    @Getter
    private List<UUID> winners;

    @Getter
    @Setter
    private String arenaName;

    @Getter
    private long dateStart;

    public Duel(UUID player1, UUID player2) {
        this.team1 = Collections.singletonList(player1);

        this.team2 = Collections.singletonList(player2);

        this.dateStart = System.currentTimeMillis();
    }

    public Duel(List<UUID> player1, List<UUID> player2) {

        this.team1 = player1;
        this.team2 = player2;

        this.dateStart = System.currentTimeMillis();
    }

    /**
     * Set the winner of the duel to the team
     *
     * @param team
     */
    public void setWinner(List<UUID> team) {
        this.winners = team;
    }

    public boolean isDamageEnabled() {
        return this.dateStart + WARMUP_TIME < System.currentTimeMillis();
    }

    public void tick() {

        if (notifiedTimes.size() != toNotify.size()) {
            for (Integer toNotify : toNotify) {

                if (!notifiedTimes.contains(toNotify)) {
                    if (this.dateStart + WARMUP_TIME - System.currentTimeMillis() <= TimeUnit.SECONDS.toMillis(toNotify)) {

                        notifiedTimes.add(toNotify);

                        Message damage_enabled_in = MainData.getIns().getMessageManager().getMessage("DAMAGE_ENABLED_IN")
                                .format("%timeLeft%", toNotify);

                        damage_enabled_in.send(this.team1);
                        damage_enabled_in.send(this.team2);

                    }
                }
            }
        }

    }

    public void addAsSpectator(UUID player) {
        this.spectators.add(player);
    }

    public boolean isSpectator(UUID player)  {
        return this.spectators.contains(player);
    }

    @Override
    public int compareTo(Duel duel) {
        return Long.compare(this.dateStart, duel.getDateStart());
    }
}
