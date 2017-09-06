package com.nuno1212s.homes.homemanager;

import com.nuno1212s.homes.main.Main;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles homes for player
 */
public class HomeManager {

    private Map<UUID, List<Home>> playerHomes = new ConcurrentHashMap<>();

    @Getter
    private long timeNeeded = 5;

    @Getter
    private HomeTimer timer;

    public HomeManager() {
        this.timer = new HomeTimer();
    }

    /**
     * Save the player homes to the playerHome map
     *
     * @param player
     * @param homes
     */
    public void registerPlayerHomes(UUID player, List<Home> homes) {
        playerHomes.put(player, homes);
    }

    /**
     * Get the player homes
     *
     * @param player
     * @return
     */
    public List<Home> getPlayerHomes(UUID player) {
        return playerHomes.getOrDefault(player, new ArrayList<>());
    }

    /**
     * Get the home with the specified name from the specified player
     *
     * @param player
     * @param homeName
     * @return
     */
    public Home getPlayerHomeWithName(UUID player, String homeName) {
        List<Home> playerHomes = getPlayerHomes(player);

        for (Home playerHome : playerHomes) {
            if (playerHome.getHomeName().equalsIgnoreCase(homeName)) {
                return playerHome;
            }
        }

        return null;
    }

    /**
     * Removes the player's homes from the RAM and saves them to the hard drive
     *
     * @param player
     */
    public void unloadPlayerHomes(UUID player) {
        Main.getIns().getFileManager().saveHomesForPlayer(player, getPlayerHomes(player));
        playerHomes.remove(player);
    }

    /**
     * Get the amount of homes for a player
     *
     * @param player
     * @return
     */
    public int getMaxAmountOfHomes(Player player) {
        String homePermission = "homes.";

        for (int i = 7; i > 0; i++) {
            if (player.hasPermission(homePermission + String.valueOf(i))) {
                return i;
            }
        }

        return 1;
    }

}
