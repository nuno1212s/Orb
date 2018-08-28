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

public class WarEvent {

    private Map<String, List<UUID>> players;

    private transient Map<String, List<UUID>> alivePlayers;

    private List<Kill> kills;

    private String winner;

    private transient WarEventHelper helper;

    public WarEvent(Map<String, List<UUID>> players, WarEventHelper helper) {
        this.players = players;

        this.alivePlayers = new HashMap<>(players);

        this.kills = new ArrayList<>();

        this.helper = helper;

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

        this.kills.add(new Kill(killer == null ? null :  killer.getUniqueId(), killed.getUniqueId()));

        for (Map.Entry<String, List<UUID>> players : alivePlayers.entrySet()) {

            if (players.getValue().contains(killed.getUniqueId())) {

                players.getValue().remove(killed.getUniqueId());

                //All of the clan's players have died
                if (players.getValue().isEmpty()) {

                    alivePlayers.remove(players.getKey());

                    checkWinConditions();

                    Clan c = ClanMain.getIns().getClanManager().getClan(players.getKey());

                    if (c != null) {
                        this.helper.sendMessage(getAllPlayers(), MainData.getIns().getMessageManager().getMessage("CLAN_ELIMINATED")
                                .format("%clanName%", c.getClanName()));
                    }
                }

                killed.spigot().respawn();

                killed.teleport(this.helper.getFallbackLocation());

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
        // TODO: 27-08-2018 Give rewards

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