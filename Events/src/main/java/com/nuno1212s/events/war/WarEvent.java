package com.nuno1212s.events.war;

import com.nuno1212s.clans.ClanMain;
import com.nuno1212s.clans.clanmanager.Clan;
import com.nuno1212s.events.EventMain;
import com.nuno1212s.events.war.util.WarEventHelper;
import com.nuno1212s.main.MainData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class WarEvent {

    public transient static final int PROTECTION_TIME = 15;

    private Map<String, List<UUID>> players;

    private transient Map<String, List<UUID>> alivePlayers;

    private List<Kill> kills;

    private String winner;

    private long startDate;

    private transient WarEventHelper helper;

    private transient static List<Integer> secondsToAnnounce = Arrays.asList(15, 10, 5, 4, 3, 2, 1);

    private transient List<Integer> secondsAnnounced = new ArrayList<>();

    private transient int taskId;

    public WarEvent(Map<String, List<UUID>> players, WarEventHelper helper) {
        this.startDate = System.currentTimeMillis();

        this.players = players;

        this.alivePlayers = new HashMap<>(players);

        this.kills = new ArrayList<>();

        this.helper = helper;


        taskId = MainData.getIns().getScheduler().runTaskTimerAsync(this::doTick, 0, 20);

    }

    public void doTick() {

        //TODO: make this a separate method in war event helper to prevent spaghetti code
        for (int time : secondsToAnnounce) {
            if (System.currentTimeMillis() - this.startDate >= TimeUnit.SECONDS.toMillis(time)) {
                if (!this.secondsAnnounced.contains(time)) {

                    this.secondsAnnounced.add(time);

                    this.helper.sendMessage(this.getAllPlayers(),
                            MainData.getIns().getMessageManager().getMessage("PVP_UNLOCKED_IN")
                                    .format("%seconds%", time));

                }
            }
        }

        if (System.currentTimeMillis() - this.startDate >= TimeUnit.SECONDS.toMillis(PROTECTION_TIME)) {

            this.helper.sendMessage(this.getAllPlayers(),
                    MainData.getIns().getMessageManager().getMessage("PVP_UNLOCKED"));

            Bukkit.getScheduler().cancelTask(taskId);

        }

    }

    /**
     * Is a clan participating in the war event
     *
     * @param clanID
     * @return
     */
    public boolean isParticipating(String clanID) {
        return this.players.containsKey(clanID);
    }

    public boolean isPlayerParticipating(UUID playerID) {
        return this.getAllPlayers().contains(playerID);
    }

    public boolean canDamage() {
        return this.startDate + TimeUnit.SECONDS.toMillis(PROTECTION_TIME) < System.currentTimeMillis();
    }

    /**
     * Kill a player
     *
     * @param killer
     * @param killed
     */
    public void kill(@Nullable Player killer, Player killed) {

        //The event has already been won, but the player left, teleport the player to the fallback location
        if (this.winner != null) {

            killed.teleport(this.helper.getFallbackLocation());

            return;

        }

        this.kills.add(new Kill(killer == null ? null : killer.getUniqueId(), killed.getUniqueId()));

        for (Map.Entry<String, List<UUID>> players : alivePlayers.entrySet()) {

            if (players.getValue().contains(killed.getUniqueId())) {

                players.getValue().remove(killed.getUniqueId());

                //All of the clan's players have died
                if (players.getValue().isEmpty()) {

                    alivePlayers.remove(players.getKey());

                    Clan c = ClanMain.getIns().getClanManager().getClan(players.getKey());

                    if (c != null) {
                        this.helper.sendMessage(getAllPlayers(), MainData.getIns().getMessageManager().getMessage("CLAN_ELIMINATED")
                                .format("%clanName%", c.getClanName()));
                    }

                    checkWinConditions();
                }

                killed.spigot().respawn();

                break;
            }

        }

    }

    private void checkWinConditions() {

        if (this.alivePlayers.size() == 1) {
            win(this.alivePlayers.entrySet().stream().findAny().orElse(new AbstractMap.SimpleEntry<>("", new ArrayList<>())).getKey());
        } else if (this.alivePlayers.isEmpty()) {
            //?????
        }

    }

    /**
     * Force the end
     */
    public void forceEnd() {
        for (UUID uuid : getAllAlivePlayers()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null || player.isOnline()) {
                continue;
            }

            player.teleport(this.helper.getFallbackLocation());
        }
    }

    private void win(String clanID) {

        this.winner = clanID;

        List<UUID> alivePlayersForClan = this.getSignedUpPlayerForClan(clanID);

        long coinReward = Math.floorDiv(this.helper.getCoinReward(), alivePlayersForClan.size());

        alivePlayersForClan.forEach((player) -> {

            MainData.getIns().getServerCurrencyHandler().addCurrency(player, coinReward);

            Player player1 = Bukkit.getPlayer(player);

            if (player1 == null || !player1.isOnline()) {
                return;
            }

            MainData.getIns().getMessageManager().getMessage("CLAN_EVENT_WON")
                    .format("%coinAmount%", coinReward).sendTo(player1);
        });

        MainData.getIns().getScheduler().runTaskLater(() -> {

            this.getAlivePlayersForClan(clanID).forEach((player) -> {

                Player player1 = Bukkit.getPlayer(player);

                if (player1 == null || !player1.isOnline()) {
                    return;
                }

                player1.teleport(this.helper.getFallbackLocation());

            });

            EventMain.getIns().getWarEvent().handleEnd();
        }, 60L);

    }

    /**
     * Get the players alive for the clan
     *
     * @param clan
     * @return
     */
    public List<UUID> getAlivePlayersForClan(String clan) {
        return this.alivePlayers.get(clan);
    }

    public List<UUID> getSignedUpPlayerForClan(String clan) {
        return this.players.get(clan);
    }

    public List<UUID> getAllAlivePlayers() {
        List<UUID> players = new ArrayList<>();

        this.alivePlayers.values().forEach(players::addAll);

        return players;
    }

    public List<UUID> getAllPlayers() {
        List<UUID> players = new ArrayList<>();

        this.players.values().forEach(players::addAll);

        return players;
    }

}

@AllArgsConstructor
@Getter
class Kill {

    UUID killer, killed;

    long time;

    public Kill(UUID killer, UUID killed) {
        this.killer = killer;
        this.killed = killed;

        this.time = System.currentTimeMillis();
    }

}