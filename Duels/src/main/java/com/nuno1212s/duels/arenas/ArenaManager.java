package com.nuno1212s.duels.arenas;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.util.LLocation;
import com.nuno1212s.util.typeadapters.LocationTypeAdapter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ArenaManager {

    private List<Arena> arenas;

    private File dataFile;

    private Gson dataHandler;

    public ArenaManager(Module module) {

        this.arenas = new ArrayList<>();

        dataFile = new File(module.getDataFolder(), "arenas.json");

        dataHandler = new GsonBuilder().registerTypeAdapter(LLocation.class, new LocationTypeAdapter())
                .create();

        if (!dataFile.exists()) {
            return;
        }

        loadArenas();
    }

    private void loadArenas() {

        try (FileReader reader = new FileReader(dataFile)) {

            Type t = new TypeToken<List<Arena>>(){}.getRawType();

            arenas = dataHandler.fromJson(reader, t);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addArena(Arena a) {

        this.arenas.add(a);

    }

    /**
     * Gets an arena by its given name
     * @param arenaName
     * @return
     */
    public Arena getArenaByName(String arenaName) {

        for (Arena arena : this.arenas) {
            if (arena.getArenaName().equalsIgnoreCase(arenaName)) {
                return arena;
            }
        }

        return null;
    }

    public void save() {

        try (FileWriter writer = new FileWriter(dataFile)) {

            this.dataHandler.toJson(this.arenas, writer);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Arena getClearArena() {

        for (Arena arena : this.arenas) {
            if (!arena.isOcupied()) {
                return arena;
            }
        }

        return null;
    }

    public void clearArena(Arena arena) {

        arena.clearArena();

    }

}
