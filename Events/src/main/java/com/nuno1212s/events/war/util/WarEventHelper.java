package com.nuno1212s.events.war.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nuno1212s.events.war.WarEvent;
import com.nuno1212s.messagemanager.Message;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.util.SerializableLocation;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class WarEventHelper {

    @Getter
    @Setter
    private Location spectatorLocation, fallbackLocation;

    private List<Location> spawns;

    private File dataFile;

    private List<WarEvent> previousWarEvents;

    private Random random;

    public WarEventHelper(Module m) {

        this.random = new Random();

        dataFile = new File(m.getDataFolder(), "warFile.json");

        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            spawns = new ArrayList<>();

            spectatorLocation = Bukkit.getWorlds().get(0).getSpawnLocation();

            fallbackLocation = Bukkit.getWorlds().get(0).getSpawnLocation();

            previousWarEvents = new ArrayList<>();
        } else {
            load();
        }

    }

    private void load() {
        try (Reader r = new FileReader(this.dataFile)) {

            JSONObject object = (JSONObject) new JSONParser().parse(r);

            this.spectatorLocation = new SerializableLocation((JSONObject) object.get("SpectatorLocation"));
            this.fallbackLocation = new SerializableLocation((JSONObject) object.get("FallbackLocation"));

            this.spawns = new ArrayList<>();

            JSONArray spawns = (JSONArray) object.get("Spawns");

            for (Object spawn : spawns) {

                if (spawn instanceof JSONObject) {
                    this.spawns.add(new SerializableLocation((JSONObject) spawn));
                }

            }

            Gson gson = new GsonBuilder().create();

            Type type = new TypeToken<List<WarEvent>>(){}.getType();

            String warEvents = (String) object.get("WarEvents");

            this.previousWarEvents = gson.fromJson(warEvents, type);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void save() {

        if (!this.dataFile.exists()) {
            try {
                this.dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (Writer fr = new FileWriter(this.dataFile)) {

            JSONObject data = new JSONObject();

            JSONObject spectatorLocation = new JSONObject(), fallbackLocation = new JSONObject();
            new SerializableLocation(this.spectatorLocation).save(spectatorLocation);
            new SerializableLocation(this.fallbackLocation).save(fallbackLocation);

            data.put("SpectatorLocation", spectatorLocation);

            data.put("FallbackLocation", fallbackLocation);

            JSONArray array = new JSONArray();

            this.spawns.forEach(location -> {

                JSONObject obj = new JSONObject();

                new SerializableLocation(location).save(obj);

                array.add(obj);

            });

            data.put("SpawnLocations", array);

            Gson gson = new GsonBuilder().create();

            data.put("WarEvents", gson.toJson(this.previousWarEvents));

            data.writeJSONString(fr);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Save a previous war event
     * @param event
     */
    public void addPrevious(WarEvent event) {
        this.previousWarEvents.add(event);
    }

    public void addSpawn(Location l) {
        this.spawns.add(l);
    }

    /**
     * Gets a random spawn location from the spawns
     *
     * @return
     */
    public Location getRandomSpawnLocation() {

        if (this.spawns.isEmpty()) {
            return null;
        }

        return this.spawns.get(this.random.nextInt(this.spawns.size()));
    }

    public void sendMessage(List<UUID> players, Message message) {

        players.forEach((playerID) -> {

            Player player = Bukkit.getPlayer(playerID);

            if (player == null || !player.isOnline()) {
                return;
            }

            message.sendTo(player);
        });

    }
}
