package com.nuno1212s.machines.machinemanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nuno1212s.machines.main.Main;
import com.nuno1212s.util.LLocation;
import com.nuno1212s.util.typeadapters.LocationTypeAdapter;
import lombok.Getter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MachineManager {

    private Gson storage;

    private File storageFile;

    @Getter
    private List<Machine> machines;

    public MachineManager() {
        storageFile = new File(Main.getIns().getDataFolder(), "machines.json");

        storage = new GsonBuilder()
                .registerTypeAdapter(LLocation.class, new LocationTypeAdapter())
                .create();

        try (FileReader r = new FileReader(storageFile)) {

            Type type = new TypeToken<List<Machine>>() {
            }.getType();

            List<Machine> machines = storage.fromJson(r, type);

            if (machines == null) {
                this.machines = Collections.synchronizedList(new ArrayList<>());
            } else {
                this.machines = Collections.synchronizedList(machines);
            }

        } catch (IOException e) {
            e.printStackTrace();

            if (machines == null) {
                this.machines = Collections.synchronizedList(new ArrayList<>());
            }
        }

    }

    public void save() {

        try (FileWriter write = new FileWriter(this.storageFile)) {

            storage.toJson(this.machines, write);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
