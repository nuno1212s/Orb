package com.nuno1212s.bungee.automessenger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.rediscommunication.Message;
import lombok.Getter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class AutoMessageManager {

    @Getter
    private Map<Integer, List<String>> messages;

    @Getter
    private int timeBetweenMessagesInTicks;

    private transient Gson gson;

    private transient File storageFile, configFile;

    public AutoMessageManager(Module m) {
        gson = new GsonBuilder().create();

        storageFile = m.getFile("automessages.json", false);

        configFile = m.getFile("autoMessageConfig.json", true);

        load();

        new MessageTimer(this);

    }

    /**
     * Load the messages from the storage file
     */
    public void load() {
        try (FileReader reader = new FileReader(storageFile);
            FileReader configReader = new FileReader(configFile)) {
            Type listType = new TypeToken<HashMap<Integer, List<String>>>(){}.getType();

            this.messages = gson.fromJson(reader, listType);

            JSONObject config = (JSONObject) new JSONParser().parse(configReader);

            this.timeBetweenMessagesInTicks = ((Long) config.getOrDefault("TimeBetweenMessagesTicks", 1200)).intValue();

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            if (this.messages == null) {
                this.messages = new HashMap<>();
            }
        }
    }

    /**
     * Save the messages
     */
    public void save() {
        if (!storageFile.exists()) {
            try {
                storageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileWriter writer = new FileWriter(this.storageFile)) {
            this.gson.toJson(this.messages, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the message corresponding to an id
     *
     * @param id
     * @return
     */
    public List<String> getMessage(int id) {
        return messages.getOrDefault(id, null);
    }

    /**
     * Add a message to the message list
     *
     * @param message
     * @return
     */
    public int addMessage(List<String> message) {
        int id = this.messages.size();
        this.messages.put(id, message);

        return id;
    }

    /**
     * Remove the message and adjust the ids
     *
     * @param id
     */
    public void removeMessage(int id) {
        this.messages.remove(id);
        //Reorganize the ids
        for (; id < messages.size(); id++) {
            this.messages.put(id, getMessage(id + 1));
            removeMessage(id + 1);
        }
    }

    private Random random = new Random();

    /**
     * Get a random message to broadcast
     * @return
     */
    public List<String> getRandomMessage() {
        if (this.messages.isEmpty()) {
            return new ArrayList<>();
        }

        return getMessage(random.nextInt(this.messages.size()));
    }

}
