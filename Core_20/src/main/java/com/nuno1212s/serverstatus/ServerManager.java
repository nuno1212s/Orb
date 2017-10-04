package com.nuno1212s.serverstatus;

import com.nuno1212s.main.MainData;
import com.nuno1212s.util.Callback;
import com.nuno1212s.util.Pair;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages servers
 */
@Getter
public class ServerManager {

    @Setter
    private String serverName, serverType;

    @Getter
    private SRedisHandler redisHandler;

    private File dataFile;

    @Getter
    private Map<String, Pair<Integer, Integer>> serverPlayerCounts;

    public ServerManager(File dataFolder) {
        this.dataFile = new File(dataFolder, "serverInfo.json");
        this.serverPlayerCounts = new HashMap<>();
        this.redisHandler = new SRedisHandler();

        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            serverName = "Default";
            serverType = "Default";
            return;
        }

        JSONObject json;

        try (FileReader in = new FileReader(dataFile)) {
            json = (JSONObject) new JSONParser().parse(in);
        } catch (IOException | ParseException e) {
            this.serverName = "Default";
            this.serverType = "Default";
            e.printStackTrace();
            return;
        }

        this.serverName = (String) json.get("ServerName");
        this.serverType = (String) json.get("ServerType");

    }

    /**
     * Save the player count of this server
     */
    public void savePlayerCount(int playerCount) {
        MainData.getIns().getScheduler().runTaskAsync(() ->
                this.getRedisHandler().updatePlayerCount(new Pair<>(playerCount, Bukkit.getMaxPlayers()))
        );
    }

    /**
     * Get the servers player counts
     */
    public void fetchServerData(Callback callback) {
        MainData.getIns().getScheduler().runTaskAsync(() -> {
                    this.serverPlayerCounts = this.getRedisHandler().getPlayerCounts();
                    callback.callback(null);
                }
        );
    }

    /**
     * Get the player count
     *
     * @param serverName
     * @return
     */
    public Pair<Integer, Integer> getPlayerCount(String serverName) {
        if (this.serverPlayerCounts.containsKey(serverName)) {
            return this.serverPlayerCounts.get(serverName);
        }

        return new Pair<>(-1, -1);
    }

    public int getTotalPlayerCount() {

        int currentPlayerCount = 0;

        for (Pair<Integer, Integer> players : this.serverPlayerCounts.values()) {
            currentPlayerCount += players.getKey();
        }

        return currentPlayerCount;
    }

    /**
     * Is something applicable in this server
     *
     * @param serverType
     * @return
     */
    public boolean isApplicable(String serverType) {
        return serverType.equalsIgnoreCase("GLOBAL") || serverType.equalsIgnoreCase(this.getServerType());
    }

    public void save() {

        getRedisHandler().removePlayerCount();

        JSONObject obj = new JSONObject();

        obj.put("ServerName", serverName);
        obj.put("ServerType", serverType);

        try (Writer writer = new FileWriter(this.dataFile)) {
            obj.writeJSONString(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
