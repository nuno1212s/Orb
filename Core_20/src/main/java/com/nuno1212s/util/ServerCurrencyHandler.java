package com.nuno1212s.util;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;

/**
 * Handles server currencies
 */
public interface ServerCurrencyHandler {

    /**
     * Get the amount of currency the player has (On the local server)
     *
     * @param playerData
     * @return
     */
    long getCurrencyAmount(PlayerData playerData);

    /**
     * Remove a specified amount of currency from the player (On the local server)
     *
     * DOES NOT AUTO UPDATE THE PLAYER SCOREBOARD (Use {@link MainData#getEventCaller()})
     *
     * @param playerData
     * @param amount
     * @return True if the removal was successful false if not
     */
    boolean removeCurrency(PlayerData playerData, long amount);

    /**
     * Add an amount of currency to the player (On the local server)
     *
     * DOES NOT AUTO UPDATE THE PLAYER SCOREBOARD (Use {@link MainData#getEventCaller()})
     *
     * @param playerData
     * @param amount
     */
    void addCurrency(PlayerData playerData, long amount);

    /**
     * Check if the player has a certain amount of currency (On the local server)
     *
     * @param playerData
     * @param amount
     * @return True if the player's money >= amount, false if not
     */
    boolean hasCurrency(PlayerData playerData, long amount);

}
