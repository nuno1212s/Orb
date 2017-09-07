package com.nuno1212s.warps.filesystem;

import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.warps.homemanager.Home;
import com.nuno1212s.warps.main.Main;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles file managing
 */
public class FileManager {

    private File dataFolder;

    public FileManager(Module m) {
        this.dataFolder = new File(m.getDataFolder() + File.separator + "DataFiles" + File.separator);

        if (!this.dataFolder.exists()) {
            this.dataFolder.mkdirs();
        }
    }

    /**
     *
     * @param player
     * @return
     */
    public File getPlayerFile(UUID player) {
        File[] files = dataFolder.listFiles();
        String playerID = player.toString();
        File playerDataFile = null;

        for (File file : files) {
            if (file.getName().startsWith(playerID)) {
                playerDataFile = file;
                break;
            }
        }

        return playerDataFile;
    }

    /**
     *
     * @param player
     * @return
     */
    public File createNewPlayerFile(UUID player) {
        File playerDataFile = new File(dataFolder, player.toString() + ".json");

        if (!playerDataFile.exists()) {
            try {
                playerDataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return playerDataFile;
    }

    /**
     * Get the player homes async
     *
     * @param player
     * @return
     */
    public void loadHomesForPlayer(UUID player) {

        MainData.getIns().getScheduler().runTaskAsync(() -> {
            File playerDataFile = getPlayerFile(player);

            if (playerDataFile == null) {
                return;
            }

            JSONObject object;

            try (FileReader r = new FileReader(playerDataFile)) {
                object = (JSONObject) new JSONParser().parse(r);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                return;
            }

            List<Home> homes = new ArrayList<>(object.size());

            for (Object homeName : object.keySet()) {
                homes.add(new Home((String) homeName, (JSONObject) object.get(homeName)));
            }

            Main.getIns().getHomeManager().registerPlayerHomes(player, homes);
        });

    }

    /**
     * Save the homes for player
     *
     * @param player
     * @param homes
     */
    public void saveHomesForPlayer(UUID player, List<Home> homes) {
        MainData.getIns().getScheduler().runTaskAsync(() -> {

            File playerFile = getPlayerFile(player);

            if (playerFile == null) {
                playerFile = createNewPlayerFile(player);
            }

            JSONObject json = new JSONObject();

            homes.forEach(home ->
                home.save(json)
            );

            try (Writer r = new FileWriter(playerFile)) {
                json.writeJSONString(r);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

}
