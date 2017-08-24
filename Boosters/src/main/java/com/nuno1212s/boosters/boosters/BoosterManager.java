package com.nuno1212s.boosters.boosters;

import com.nuno1212s.boosters.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.multipliers.main.RankMultiplierMain;
import com.nuno1212s.playermanager.PlayerData;
import lombok.Getter;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Manages all boosters
 */
public class BoosterManager {

    //TODO: The boosters must store the ID of their owner, because they need to be globally accessible

    @Getter
    private final List<Booster> boosters;

    public BoosterManager() {
        boosters = Collections.synchronizedList(new ArrayList<>());
        addBoosters(Main.getIns().getMysqlHandler().loadBoosters());
    }

    /**
     * Sorts through the boosters and saves the ones that are applicable to the server
     *
     * (Boosters which the owner is not on the server for example do not apply to this instance and therefore should not be stored in the RAM)
     *
     * @param boosters
     */
    private void addBoosters(List<Booster> boosters) {
        for (Booster booster : boosters) {
            if (booster.isApplicable(null)) {
                this.boosters.add(booster);
            }
        }
    }

    /**
     * Load all the boosters belonging to the player
     *
     * @param player
     */
    public void loadBoostersForPlayer(UUID player) {

        List<Booster> c = Main.getIns().getMysqlHandler().loadBoosters(player);
        this.boosters.addAll(c);

        for (Booster booster : c) {
            //If the booster has expired while the player was offline, expire the booster
            //No chance of ConcurrentModification because the list c is separate from the global booster list
            if (booster.isActivated() && booster.isExpired()) {
                handleBoosterExpiration(booster);
            }
        }

    }

    /**
     * Remove the boosters that are only activated when the owner is on the server, to save RAM
     * @param player
     */
    public void removeBoostersForPlayer(UUID player) {
        this.boosters.removeIf(booster ->
                booster.getOwner() != null
                && booster.getOwner().equals(player)
                && (booster.getType() == BoosterType.PLAYER_GLOBAL || booster.getType() == BoosterType.PLAYER_SERVER));
    }

    /**
     * Get a random, unused ID for a booster
     *
     * @return
     */
    private String getRandomID() {
        String random = RandomStringUtils.random(5, true, true);
        if (getBooster(random) != null) {
            return getRandomID();
        }
        return random;
    }

    /**
     * Create a booster with the given parameters
     *
     * @param owner The owner of the booster
     * @param multiplier The multiplier of the booster
     * @param durationInMillis The duration of the booster in millis
     * @param type The type of the booster
     * @param applicableServer The server where the booster can be applied
     * @param customName
     * @return
     */
    public Booster createBooster(UUID owner, float multiplier, long durationInMillis, BoosterType type, String applicableServer, String customName) {
        return new Booster(getRandomID(), owner, type, multiplier, durationInMillis, 0, false, applicableServer, customName);
    }

    /**
     * Handles the expiration of a booster
     * @param b
     */
    public void handleBoosterExpiration(Booster b) {
        //TODO: Announce booster expiration

        notifyPlayer(b, b.getOwner());
        Main.getIns().getRedisHandler().handleBoosterDeletion(b);
    }

    /**
     * Notify a player (player) that his booster (b) has expired
     * If the player is notified successfully the booster should be removed from storage
     *
     * @param b
     * @param player
     */
    public void notifyPlayer(Booster b, UUID player) {
        Player p = Bukkit.getPlayer(player);
        if (p == null) {
            return;
        }

        MainData.getIns().getMessageManager().getMessage("BOOSTER_FINISHED")
                .format("%boosterName%", b.getCustomName()).sendTo(p);

        MainData.getIns().getScheduler().runTaskAsync(() -> {
            removeBooster(b);
        });

    }

    /**
     * Handles the adding of the boosters from the redis
     *
     * @param b
     */
    public void handleBoosterAdition(Booster b) {
        this.boosters.add(b);
    }

    /**
     * Handles activating booster
     *
     * @param b
     */
    public void activateBooster(Booster b) {
        b.activate();
        Main.getIns().getRedisHandler().handleBoosterActivation(b);
    }

    /**
     * Adds a booster to the booster list
     *
     * USES REDIS TO UPDATE ALL OTHER SERVERS
     *
     * @param b
     */
    public void addBooster(Booster b) {
        this.boosters.add(b);
        Main.getIns().getRedisHandler().addBooster(b);
    }

    /**
     * Removes a booster from the booster list
     *
     * USES REDIS TO UPDATE ALL OTHER SERVERS
     *
     * @param b
     */
    public void removeBooster(Booster b) {
        this.boosters.remove(b);
        Main.getIns().getMysqlHandler().removeBooster(b.getBoosterID());
    }

    /**
     * Get the booster for the players
     *
     * @param player
     * @return
     */
    public List<Booster> getBoostersForPlayer(UUID player) {
        return this.boosters.stream().filter(b -> b.getOwner() != null && b.getOwner().equals(player)).collect(Collectors.toList());
    }

    /**
     * Get the booster for the booster ID
     *
     * @param boosterID The ID of the booster
     * @return
     */
    public Booster getBooster(String boosterID) {
        synchronized (boosters) {
            for (Booster booster : boosters) {
                if (booster.getBoosterID().equalsIgnoreCase(boosterID)) {
                    return booster;
                }
            }
            return null;
        }
    }

    /**
     * Get the rank multiplier added to the active boosters
     *
     * @param data
     * @return
     */
    public double getFinalMultiplierForPlayer(PlayerData data) {
        double currentBooster = RankMultiplierMain.getIns().getRankManager().getGlobalMultiplier().getRankMultiplierForPlayer(data);

        synchronized (boosters) {
            for (Booster booster : boosters) {
                if (booster.isActivated() && booster.isApplicable(data.getPlayerID())) {
                    currentBooster += booster.getMultiplier();
                }
            }
        }

        return currentBooster;
    }

    /**
     * Get the active boosters multiplier
     *
     * @param data
     * @return
     */
    public double getBoosterMultiplierForPlayer(PlayerData data) {
        double currentBooster = 0D;

        synchronized (boosters) {
            for (Booster booster : boosters) {
                if (booster.isActivated() && booster.isApplicable(data.getPlayerID())) {
                    currentBooster += booster.getMultiplier();
                }
            }
        }

        return currentBooster;
    }

    /**
     * Check if a player has an active booster
     *
     * @param data
     * @return
     */
    public boolean isBoosterActive(PlayerData data) {

        synchronized (boosters) {
            for (Booster booster : boosters) {
                if (booster.isActivated() && booster.isApplicable(data.getPlayerID())) {
                    return true;
                }
            }
        }

        return false;
    }

}
