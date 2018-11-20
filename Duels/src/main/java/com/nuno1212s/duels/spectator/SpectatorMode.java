package com.nuno1212s.duels.spectator;

import com.nuno1212s.duels.duelmanager.Duel;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpectatorMode {

    private Duel parentDuel;

    public SpectatorMode(Duel duel) {

        this.parentDuel = duel;

    }

    public void setAsSpectator(Player player) {

        List<UUID> team1 = parentDuel.getTeam1(), team2 = parentDuel.getTeam2();

        showPlayerToSpectators(player);

        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(player.getMaxHealth());
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setFoodLevel(20);
        player.setFireTicks(0);

        //Hide the player from the teams
        hidePlayer(player, team1);
        hidePlayer(player, team2);

        //Make the player not collide with entities
        player.spigot().setCollidesWithEntities(false);
    }

    public void removeSpectator(Player player) {

        showPlayer(player, parentDuel.getTeam1());
        showPlayer(player, parentDuel.getTeam2());

        player.spigot().setCollidesWithEntities(true);

        player.setFlying(false);
        player.setAllowFlight(false);

    }

    public void handlePlayerLeaveArena(UUID player) {

        Player p = Bukkit.getPlayer(player);

        showPlayerToSpectators(p);
    }

    public void endGame() {

        List<UUID> spectatorID = parentDuel.getSpectators();

        List<Player> spectators = new ArrayList<>(spectatorID.size());

        for (UUID uuid : spectatorID) {
            spectators.add(Bukkit.getPlayer(uuid));
        }

        List<UUID> team1 = parentDuel.getTeam1(), team2 = parentDuel.getTeam2();

        showSpectatorsToTeam(spectators, team1);

        showSpectatorsToTeam(spectators, team2);

    }

    private void showSpectatorsToTeam(List<Player> spectators, List<UUID> team2) {
        for (UUID uuid : team2) {

            Player player = Bukkit.getPlayer(uuid);

            if (player == null || !player.isOnline()) {
                continue;
            }

            for (Player p2 : spectators) {

                if (!player.canSee(p2)) {
                    player.showPlayer(p2);
                }

            }

        }
    }

    private void showPlayerToSpectators(Player p) {
        for (UUID spectator : parentDuel.getSpectators()) {

            PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(spectator);

            Player playerReference = playerData.getPlayerReference(Player.class);

            if (!p.canSee(playerReference)) {

                p.showPlayer(playerReference);

            }

        }
    }

    private void showPlayer(Player player, List<UUID> players) {
        for (UUID uuid : players) {

            Player p = Bukkit.getPlayer(uuid);

            if (p == null || !p.isOnline()) {
                continue;
            }

            p.showPlayer(player);
        }
    }

    private void hidePlayer(Player player, List<UUID> players) {

        for (UUID uuid : players) {

            Player p = Bukkit.getPlayer(uuid);

            if (p == null || !p.isOnline()) {
                continue;
            }

            p.hidePlayer(player);
        }
    }

}
