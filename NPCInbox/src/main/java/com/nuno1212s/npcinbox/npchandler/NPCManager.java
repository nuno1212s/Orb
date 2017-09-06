package com.nuno1212s.npcinbox.npchandler;

import com.nuno1212s.modulemanager.Module;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Manages NPCs
 */
public class NPCManager {

    private List<UUID> npcIds;

    private File file;

    public NPCManager(Module m) {

        this.npcIds = new ArrayList<>();

        file = m.getFile("npcs.json", false);

        try (Reader r = new FileReader(file)) {
            JSONObject obj = (JSONObject) new JSONParser().parse(r);
            List<String> npcs = (List<String>) obj.get("NPCS");

            npcs.forEach(id -> this.npcIds.add(UUID.fromString(id)));

        } catch (IOException | ParseException e) {
            System.out.println("Failed to read NPC file. First time running?");
        }

    }

    /**
     * Save the entities
     */
    public void save() {
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        for (UUID npcId : npcIds) {
            array.add(npcId.toString());
        }
        json.put("NPCS", array);
        try (Writer w = new FileWriter(file)) {
            json.writeJSONString(w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Is the NPC registered as an Inbox entity
     *
     * @param npcID
     * @return
     */
    public boolean isNPCRegistered(UUID npcID) {
        return this.npcIds.contains(npcID);
    }

    /**
     * Register an entity as an Inbox entity
     *
     * @param npcID
     */
    public void registerNPC(UUID npcID) {
        this.npcIds.add(npcID);
    }

    /**
     * Unregister an entity as an Inbox entity
     *
     * @param npcID
     */
    public void unregisterNPC(UUID npcID) {
        this.npcIds.remove(npcID);
    }

    /**
     * Get the entity in a player's line of sight.
     *
     * @param player
     * @param distance
     * @return
     */
    public Entity getEntityInLineOfSight(Player player, int distance) {
        Vector direction = player.getLocation().getDirection().normalize();

        Location eyeLocation = player.getEyeLocation().clone();

        for (int i = 0; i < distance; i++) {
            eyeLocation.add(direction);
            Collection<Entity> nearbyEntities = eyeLocation.getWorld().getNearbyEntities(eyeLocation, 1, 1, 1);
            for (Entity nearbyEntity : nearbyEntities) {
                if (!nearbyEntity.getUniqueId().equals(player.getUniqueId())) {
                    return nearbyEntity;
                }
            }
        }

        return null;
    }

}