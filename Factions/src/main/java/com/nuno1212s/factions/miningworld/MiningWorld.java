package com.nuno1212s.factions.miningworld;

import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.util.Callback;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MiningWorld implements Listener {

    private String worldName;

    @Getter
    private long lastReset, betweenResets;

    private int size;

    @Getter
    private transient boolean isLoading;

    @Getter
    private transient World currentMiningWorld;

    private transient File dataFile;

    public MiningWorld(Module m) {

        dataFile = m.getFile("worldData.json", false);
        File file = m.getFile("worldConfig.json", true);

        try (Reader r = new FileReader(file); Reader fR = new FileReader(dataFile)) {
            JSONObject config = (JSONObject) new JSONParser().parse(r), dataJSON = (JSONObject) new JSONParser().parse(fR);

            this.worldName = (String) config.getOrDefault("WorldName", "MiningWorld");
            this.size = ((Long) config.getOrDefault("Size", 2000)).intValue();
            this.betweenResets = (Long) config.getOrDefault("BetweenResets", TimeUnit.DAYS.toMillis(2));

            this.lastReset = (Long) dataJSON.getOrDefault("LastReset", 0);
            this.isLoading = (Boolean) dataJSON.getOrDefault("IsLoading", false);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        new WorldExpireTask();

        BukkitMain.getIns().getServer().getPluginManager().registerEvents(this, BukkitMain.getIns());

        createWorld((world) -> {
            currentMiningWorld = world;
            lastReset = System.currentTimeMillis();
            isLoading = false;
        });
    }

    /**
     * Deletes and generates the mining world
     */
    public void regenerateWorld() {
        isLoading = true;
        MainData.getIns().getScheduler().runTask(() ->
                deleteWorld((arg) -> {
                    createWorld((world) -> {
                        currentMiningWorld = world;
                        lastReset = System.currentTimeMillis();
                        isLoading = false;
                    });
                })
        );
    }

    /**
     * Create a world with the given parameters
     *
     * @param world
     */
    public void createWorld(Callback<World> world) {
        isLoading = true;
        MainData.getIns().getScheduler().runTaskAsync(() -> {

            WorldCreator w = new WorldCreator(this.worldName);
            World worldToGenerate = w.createWorld();
            WorldBorder worldBorder = worldToGenerate.getWorldBorder();
            worldBorder.setSize(this.size);
            worldBorder.setCenter(worldToGenerate.getSpawnLocation());

            loadChunks(worldToGenerate, world);

        });
    }

    /**
     * Load the chunks from a world
     *
     * @param w
     * @param world
     */
    private void loadChunks(World w, Callback<World> world) {
        new WorldCreateTask(w, world, size);
    }

    /**
     * Delete the current mining world
     *
     * @param finished
     */
    public void deleteWorld(Callback finished) {

        for (Player player : currentMiningWorld.getPlayers()) {
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }

        MainData.getIns().getScheduler().runTaskAsync(() -> {
            Bukkit.getServer().unloadWorld(currentMiningWorld, false);

            deleteWorld(currentMiningWorld);
            finished.callback(null);
        });
    }

    /**
     * Delete a worlds folder
     *
     * @param w
     */
    private void deleteWorld(World w) {
        File worldFolder = w.getWorldFolder();
        deleteFile(worldFolder);
    }

    /**
     * Delete a file
     *
     * @param f
     */
    private void deleteFile(File f) {
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                deleteFile(file);
            }
        } else {
            f.delete();
        }
    }

    /**
     * Save the data to the data
     */
    public void saveToFile() {
        try (Writer r = new FileWriter(dataFile)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("LastReset", this.lastReset);
            jsonObject.put("IsLoading", this.isLoading);

            jsonObject.writeJSONString(r);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onWorldLoad(WorldInitEvent e) {
        e.getWorld().setKeepSpawnInMemory(false);
    }

    Random r = new Random();

    public Location getRandomSpawnLocation() {

        double x = r.nextDouble() * (this.size / 2), z = r.nextDouble() * (this.size / 2);

        int y = this.getCurrentMiningWorld().getHighestBlockYAt((int) x, (int) z) + 2;

        Location l = new Location(this.getCurrentMiningWorld(), x, y, z);

        Biome biome = this.getCurrentMiningWorld().getBiome((int) x, (int) z);

        if (biome == Biome.OCEAN || biome == Biome.DEEP_OCEAN) {
            return getRandomSpawnLocation();
        }

        return l;
    }

}
