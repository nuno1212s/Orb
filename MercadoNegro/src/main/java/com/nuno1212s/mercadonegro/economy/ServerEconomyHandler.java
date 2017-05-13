package com.nuno1212s.mercadonegro.economy;

import java.util.UUID;

/**
 * Handles server economy
 */
public interface ServerEconomyHandler {

    boolean charge(UUID player, long price);

    long getBalance(UUID player);

}
