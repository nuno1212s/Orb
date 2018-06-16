package com.nuno1212s.sellsigns.signs;

import com.nuno1212s.modulemanager.Module;
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
import java.util.UUID;

/**
 * Sign manager
 */
public class SignManager {

    private List<StoreSign> stores;

    private File file;

    private List<UUID> editing;

    public SignManager(Module m) {
        editing = new ArrayList<>();
        stores = new ArrayList<>();
        file = m.getFile("stores.json", false);

        JSONObject json;
        try (FileReader r = new FileReader(file)) {
            json = (JSONObject) new JSONParser().parse(r);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }

        json.keySet().forEach(sign -> {
            stores.add(new StoreSign((JSONObject) json.get(sign)));
        });

    }

    public int getNextID() {
        int currentID = 0;
        for (StoreSign store : stores) {
            if (store.getId() > currentID) {
                currentID = store.getId();
            }
        }
        return ++currentID;
    }

    public boolean isEditing(UUID editing) {
        return this.editing.contains(editing);
    }

    public void addToEditing(UUID player) {
        this.editing.add(player);
    }

    public void removeFromEditing(UUID player) {
        this.editing.remove(player);
    }

    public void addSign(StoreSign sign) {
        this.stores.add(sign);
    }

    public StoreSign getSign(Location l) {
        for (StoreSign store : this.stores) {
            if (store.equalsLocation(l)) {
                return store;
            }
        }
        return null;
    }

    public void removeSign(StoreSign sign) {
        this.stores.remove(sign);
    }

    public void saveSigns() {
        JSONObject object = new JSONObject();

        stores.forEach(store -> {
            JSONObject o = new JSONObject();
            object.put(String.valueOf(store.getId()), store.save(o));
        });

        try (FileWriter r = new FileWriter(file)) {
            object.writeJSONString(r);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
