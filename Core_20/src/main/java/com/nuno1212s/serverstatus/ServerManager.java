package com.nuno1212s.serverstatus;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

/**
 * Manages servers
 */
@Getter
public class ServerManager {

    @Setter
    private String serverName, serverType;

    private File dataFile;

    public ServerManager(File dataFolder) {
        dataFile = new File(dataFolder, "serverInfo.json");
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
            e.printStackTrace();
            return;
        }

        this.serverName = (String) json.get("ServerName");
        this.serverType = (String) json.get("ServerType");

    }

    public void save() {
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
