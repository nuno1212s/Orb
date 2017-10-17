package com.nuno1212s.warps.tpamanager;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TPAManager {

    /**
     * The active TPAs requests
     */
    private Map<UUID, TPAInstance> tpas;

    @Getter
    private long timeDelay;

    public TPAManager() {
        tpas = new HashMap<>();
    }

    /**
     * Register a teleport for the player
     *
     * @param playerID
     * @param teleportInstance
     */
    public void registerTeleport(UUID playerID, TPAInstance teleportInstance) {
        this.tpas.put(playerID, teleportInstance);
    }

    public boolean isPlayerTeleporting(UUID playerID) {
        return this.tpas.containsKey(playerID);
    }

    public TPAInstance getTeleport(UUID player) {
        return this.tpas.get(player);
    }

}
