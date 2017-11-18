package com.nuno1212s.npcinbox.npchandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Manages NPCs
 */
public class NPCManager {

    private List<NPC> npcs;

    private File file;

    private Gson gson;

    public NPCManager(Module m) {

        this.gson = new GsonBuilder().create();

        file = m.getFile("npcs.json", false);

        try (Reader r = new FileReader(file)) {

            Type t = new TypeToken<List<NPC>>(){}.getType();

            this.npcs = gson.fromJson(r, t);
        } catch (IOException | JsonParseException e) {
            System.out.println("Failed to read NPC file. First time running?");
        } finally {
            if (this.npcs == null) {
                this.npcs = new ArrayList<>();
            }
        }

    }

    /**
     * Save the entities
     */
    public void save() {
        try (Writer w = new FileWriter(file)) {
            gson.toJson(this.npcs, w);
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
        for (NPC npc : this.npcs) {
            if (npc.getEntityID().equals(npcID)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Register an entity as an Inbox entity
     *
     * @param npcID
     */
    public void registerNPC(UUID npcID) {
        this.npcs.add(new NPC(npcID));
    }

    /**
     * Unregister an entity as an Inbox entity
     *
     * @param npcID
     */
    public void unregisterNPC(UUID npcID) {
        //We can use the NPC ID on the remove method because of the custom equals on the NPC class
        this.npcs.remove(npcID);
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


    /**
     * Display reward notifications for a player
     * @param data
     */
    public void displayNotificationsForPlayer(PlayerData data) {
        npcs.forEach((npc) -> npc.displayInformation(data));
    }

    public void removeHologramsForPlayer(PlayerData data) {
        npcs.forEach((npc) -> npc.removeHologram(data));
    }

}