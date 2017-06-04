package com.nuno1212s.util;

import com.nuno1212s.playermanager.PlayerData;

/**
 * Handles server currencies
 */
public interface ServerCurrencyHandler {

    long getCurrencyAmount(PlayerData playerData);

    boolean removeCurrency(PlayerData playerData, long amount);

    void addCurrency(PlayerData playerData, long amount);

    boolean hasCurrency(PlayerData playerData, long amount);

}
