package com.nuno1212s.machines.machinemanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nuno1212s.machines.main.Main;
import com.nuno1212s.util.LLocation;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import com.nuno1212s.util.SerializableItem;
import com.nuno1212s.util.typeadapters.ItemStackTypeAdapter;
import com.nuno1212s.util.typeadapters.LocationTypeAdapter;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class MachineManager {

    private Gson storage;

    private File storageFile, configFile;

    @Getter
    private List<Machine> machines;

    private Map<Integer, MachineConfiguration> configurations;

    @Getter
    private ItemStack statsItem;

    public MachineManager() {
        storageFile = new File(Main.getIns().getDataFolder(), "machines.json");
        configFile = new File(Main.getIns().getDataFolder(), "configFile.json");

        storage = new GsonBuilder()
                .registerTypeAdapter(LLocation.class, new LocationTypeAdapter())
                .registerTypeAdapter(ItemStack.class, new ItemStackTypeAdapter())
                .create();

        loadConfig();

        loadConfigurations();

        loadMachines();
    }

    private void loadConfig() {

        try (FileReader f = new FileReader(this.configFile)) {

            JSONObject parsed = (JSONObject) new JSONParser().parse(f);

            this.statsItem = new SerializableItem((JSONObject) parsed.get("StatsItem"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void loadConfigurations() {

        configurations = new HashMap<>();
        File machineConfigurations = new File(Main.getIns().getDataFolder(), "machineConfigurations.json");

        if (!machineConfigurations.exists()) {
            Main.getIns().saveResource(machineConfigurations, "machineConfigurations.json");
        }

        try (FileReader fr = new FileReader(machineConfigurations)) {

            JSONArray parse = (JSONArray) new JSONParser().parse(fr);

            for (Object object : parse) {
                MachineConfiguration configuration = new MachineConfiguration((JSONObject) object);

                this.configurations.put(configuration.getId(), configuration);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadMachines() {
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

    public MachineConfiguration getConfiguration(int id) {
        return this.configurations.get(id);
    }

    /**
     * Get a machine with a specific ID
     *
     * @param machineID
     * @return
     */
    public Machine getMachineWithID(UUID machineID) {
        synchronized (machines) {
            for (Machine m : machines) {
                if (m.getMachineID().equals(machineID)) {
                    return m;
                }
            }
        }

        return null;
    }

    /**
     * Get's the machine at the given location
     *
     * @param l
     * @return
     */
    public Machine getMachineAtLocation(Location l) {

        synchronized (machines) {
            for (Machine m : this.machines) {
                if (m.getMachineLocation().equals(l)) {
                    return m;
                }
            }
        }

        return null;
    }

    /**
     * Create a machine with the given parameters
     *
     * @param placingPlayer The player that placed the machine
     * @param placedBlock   The machine block that was placed
     * @param item
     * @return
     */
    public Machine getMachineFromItem(Player placingPlayer, Block placedBlock, ItemStack item) {
        MachineConfiguration mC = MachineConfiguration.fromItem(item);

        if (mC == null) {
            return null;
        }

        return new Machine(placingPlayer.getUniqueId(), mC, new LLocation(placedBlock.getLocation()));
    }

    public boolean isMachine(ItemStack item) {
        NBTCompound nbt = new NBTCompound(item);

        return nbt.getValues().containsKey("Machine");
    }

    /**
     * Saves the machines
     */
    public void save() {

        try (FileWriter write = new FileWriter(this.storageFile)) {

            synchronized (this.machines) {
                storage.toJson(this.machines, write);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void registerMachine(Machine machineFromItem) {

        this.machines.add(machineFromItem);

    }

    public void unregisterMachine(Machine m) {

        this.machines.remove(m);

    }
}
