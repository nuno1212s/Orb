package com.nuno1212s.warps.tpamanager;

import com.nuno1212s.modulemanager.Module;
import lombok.Getter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
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

    public TPAManager(Module m) {
        tpas = new HashMap<>();

        File configFile = m.getFile("tpaConfig.json", true);

        try (Reader r = new FileReader(configFile)) {
            JSONObject jsonConfig = (JSONObject) new JSONParser().parse(r);
            this.timeDelay = (Long) jsonConfig.getOrDefault("Delay", 3);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * Register a teleport for the player
     *
     * @param playerID The ID of the player that requested the teleport
     * @param teleportInstance
     */
    public void registerTeleport(UUID playerID, TPAInstance teleportInstance) {
        //Check if the recipient already has a pending teleport
        if (this.getTeleportFromRecipient(teleportInstance.getTarget().getPlayerID()) != null) {
            this.removeTeleportFromRecipient(teleportInstance.getTarget().getPlayerID());
        }

        this.tpas.put(playerID, teleportInstance);
        teleportInstance.notifyCreation();

    }

    public boolean isPlayerTeleporting(UUID playerID) {
        return this.tpas.containsKey(playerID);
    }

    /**
     * Get teleport for a recipient
     *
     * @param recipient
     * @return
     */
    public TPAInstance getTeleportFromRecipient(UUID recipient) {
        for (TPAInstance tpaInstance : this.tpas.values()) {
            if (tpaInstance.getTarget().getPlayerID().equals(recipient)) {
                return tpaInstance;
            }
        }

       return null;
    }

    /**
     * Remove a teleport request to a recipient
     *
     * @param recipient
     */
    public void removeTeleportFromRecipient(UUID recipient) {
        for (Map.Entry<UUID, TPAInstance> tpa : this.tpas.entrySet()) {
            if (tpa.getValue().getTarget().getPlayerID().equals(recipient)) {
                this.tpas.remove(tpa.getKey());
                break;
            }
        }
    }

    public void removeTeleportFromSender(UUID sender) {
        for (Map.Entry<UUID, TPAInstance> tpa : this.tpas.entrySet()) {
            if (tpa.getValue().getToTeleport().getPlayerID().equals(sender)) {
                this.tpas.remove(tpa.getKey());
                break;
            }
        }
    }

    /**
     * Get the teleport for a player
     *
     * @param player The player that requested the teleported, not the player that received the request
     * @return
     */
    public TPAInstance getTeleport(UUID player) {
        return this.tpas.get(player);
    }

}
