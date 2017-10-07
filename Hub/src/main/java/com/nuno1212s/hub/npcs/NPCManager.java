package com.nuno1212s.hub.npcs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NPCManager {

    private List<NPC> registeredNPCS;

    private Gson gson;

    private File dataFile;

    public NPCManager(Module m) {
        this.gson = new GsonBuilder().create();

        this.dataFile = m.getFile("npcData.json", false);

        try (Reader r = new FileReader(dataFile)) {
            Type listType = new TypeToken<ArrayList<NPC>>(){}.getType();

            registeredNPCS = gson.fromJson(r, listType);
            if (registeredNPCS == null) {
                registeredNPCS = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the NPCs
     */
    public void save() {
        if (!this.dataFile.exists()) {
            try {
                this.dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileWriter r = new FileWriter(this.dataFile)) {
            gson.toJson(this.registeredNPCS, r);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Register an npc
     *
     * @param entity The entity of the npc
     * @param connectingServer The server you are trying to connecting to
     */
    public NPC addNPC(LivingEntity entity, String connectingServer, String displayName) {
        NPC e = new NPC(entity, connectingServer, displayName);

        this.registeredNPCS.add(e);

        return e;
    }

    /**
     * Get the NPC connected to an entity
     *
     * @param e
     * @return
     */
    public NPC getNPC(LivingEntity e) {
        for (NPC registeredNPC : this.registeredNPCS) {
            if (registeredNPC.getNpcID().equals(e.getUniqueId())) {
                return registeredNPC;
            }
        }

        return null;
    }

    /**
     * Remove the NPC from the list
     * @param c
     */
    public void removeNPC(NPC c) {
        c.deleteNPC();
        this.registeredNPCS.remove(c);
    }

    public LivingEntity getEntity(Player p) {
        LivingEntity e = null;

        Vector direction = p.getLocation().getDirection().normalize();

        Location location = p.getLocation().clone();

        fory: for (int i = 0; i <= 5; i++) {
            Location add = location.add(direction);
            Collection<Entity> nearbyEntities = add.getWorld().getNearbyEntities(add, 1, 1, 1);
            if (!nearbyEntities.isEmpty()) {
                for (Entity next : nearbyEntities) {
                    if (next instanceof LivingEntity && !next.getUniqueId().equals(p.getUniqueId())) {
                        e = (LivingEntity) next;

                        break fory;
                    }
                }
            }
        }

        return e;
    }

    /**
     * Update the NPCs
     */
    public void updateNPCs() {
        registeredNPCS.forEach(NPC::updateNPC);
    }

}
