package com.nuno1212s.duels.arenas;

import com.nuno1212s.duels.duelmanager.Duel;
import com.nuno1212s.duels.events.ArenaClearEvent;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.LLocation;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Arena {

    @Getter
    private String arenaName;

    public List<LLocation> spawns1, spawns2;

    @Getter
    private transient boolean ocupied;

    @Getter
    private transient Duel ocupying;

    public Arena(String arenaName) {
        this.arenaName = arenaName;

        this.spawns1 = new ArrayList<>();
        this.spawns2 = new ArrayList<>();
    }

    /**
     * Add spawns to the respective team
     *
      * @param spawnLocation
     */
    public void addSpawn1(LLocation spawnLocation) {
        this.spawns1.add(spawnLocation);
    }

    public void addSpawn2(LLocation spawnLocation) {
        this.spawns2.add(spawnLocation);
    }

    public void clearArena() {

        this.ocupied = false;
        this.ocupying = null;

        Bukkit.getServer().getPluginManager().callEvent(new ArenaClearEvent(this));
    }

    /**
     * Teleports the players to the arena and marks the arena as full
     *
     * @param d
     */
    public void fillArena(Duel d) {
        this.ocupied = true;

        this.ocupying = d;

        for (UUID uuid : d.getTeam1()) {

            PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(uuid);

            if (playerData == null || !playerData.isPlayerOnServer()) {
                continue;
            }

            playerData.getPlayerReference(Player.class).teleport(getRandomSpawn1().getLocation());
        }

        for (UUID uuid : d.getTeam2()) {

            PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(uuid);

            if (playerData == null || !playerData.isPlayerOnServer()) {
                continue;
            }

            playerData.getPlayerReference(Player.class).teleport(getRandomSpawn2().getLocation());
        }
    }


    private LLocation getRandomSpawn1() {
        return getRandomSpawn(this.spawns1);
    }

    private LLocation getRandomSpawn2() {
        return getRandomSpawn(this.spawns2);
    }

    private static transient Random random = new Random();

    private static LLocation getRandomSpawn(List<LLocation> spawns) {

        return spawns.get(random.nextInt(spawns.size()));

    }

}
