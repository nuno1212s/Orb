package com.nuno1212s.minas.minemanager;

import com.nuno1212s.modulemanager.Module;
import lombok.Getter;
import org.bukkit.Location;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Mine manager
 */
public class MineManager {

    @Getter
    private List<Mine> mines;

    private File storageFile;

    public MineManager(Module m) {
        this.mines = new ArrayList<>();

        storageFile = m.getFile("mines.yml", false);
        if (!storageFile.exists()) {
            try {
                storageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        loadMines();

    }

    public void addMine(Mine m) {
        this.mines.add(m);
    }

    public void removeMine(Mine m) {
        this.mines.remove(m);
    }

    public Mine getMineByID(String mineID) {
        for (Mine mine : this.mines) {
            if (mine.getMineID().equalsIgnoreCase(mineID)) {
                return mine;
            }
        }
        return null;
    }

    public boolean isInAMine(Location l) {
        for (Mine mine : this.mines) {
            if (mine.isInMine(l)) {
                return true;
            }
        }
        return false;
    }

    private void loadMines() {
        JSONObject json;

        try (FileReader f = new FileReader(this.storageFile)) {
            json = (JSONObject) new JSONParser().parse(f);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }

        for (Object o : json.keySet()) {
            mines.add(new Mine((JSONObject) json.get(o)));
        }

    }

    public void save() {
        JSONObject obj = new JSONObject();
        this.mines.forEach(mine -> {
            JSONObject j = new JSONObject();
            mine.save(j);
            obj.put(mine.getMineID(), j);
        });

        try (FileWriter f = new FileWriter(storageFile)) {
            obj.writeJSONString(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
