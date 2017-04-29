package com.nuno1212s.crates;

import java.util.UUID;

/**
 * Handles server economies
 */
public interface ServerEconomyInterface {

    boolean charge(UUID player, long cost);

}
