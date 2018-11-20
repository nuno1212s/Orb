package com.nuno1212s.duels.duelmanager;

import com.nuno1212s.duels.spectator.SpectatorMode;
import com.nuno1212s.main.MainData;
import com.nuno1212s.messagemanager.Message;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Duel implements Comparable<Duel> {

    public transient static final long WARMUP_TIME = TimeUnit.SECONDS.toMillis(15);

    private transient static final List<Integer> toNotify = Arrays.asList(15, 10, 5, 4, 3, 2, 1);

    private transient List<Integer> notifiedTimes = new ArrayList<>(toNotify.size());

    @Getter
    private transient List<UUID> spectators = new ArrayList<>();

    private transient SpectatorMode spectatorMode;

    @Getter
    private List<UUID> team1, team2;

    private transient List<UUID> alivePlayers1, alivePlayers2;

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

        this.alivePlayers1 = new ArrayList<>(player1);
        this.alivePlayers2 = new ArrayList<>(player2);

        this.dateStart = System.currentTimeMillis();

        spectatorMode = new SpectatorMode(this);
    }

    /**
     * Set the winner of the duel to the team
     *
     * @param team
     */
    public void setWinner(List<UUID> team) {
        this.winners = team;
    }

    private void win(List<UUID> team) {



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

    public void handleDeath(Player player) {

        if (alivePlayers2.contains(player.getUniqueId())) {
            alivePlayers2.remove(player.getUniqueId());
        } else {
            alivePlayers1.remove(player.getUniqueId());
        }

        if (alivePlayers1.isEmpty()) {
            win(team2);
        } else {
            win(team1);
        }

        addAsSpectator(player);

    }

    public void addAsSpectator(Player player) {

        this.spectators.add(player.getUniqueId());

        spectatorMode.setAsSpectator(player);
    }

    public void removeAsSpectator(Player player) {

        this.spectators.remove(player.getUniqueId());

        spectatorMode.removeSpectator(player);
    }

    public boolean isSpectator(UUID player) {
        return this.spectators.contains(player);
    }

    @Override
    public int compareTo(Duel duel) {
        return Long.compare(this.dateStart, duel.getDateStart());
    }
}
