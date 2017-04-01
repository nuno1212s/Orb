package com.nuno1212s.core.messagemanager;

import com.nuno1212s.core.util.Pair;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * JSON messages
 * <p>
 * Handles multiple messages
 */
public class Messages {

    private Map<String, Message> messages;

    File messageFile;

    public Messages(JavaPlugin j) {
        messageFile = new File(j.getDataFolder(), "messages.json");
        if (!messageFile.exists()) {
            j.saveResource("messages.json", false);
        }
        this.messages = reloadMessages(messageFile);
    }

    public Map<String, Message> reloadMessages(File f) {
        Map<String, Message> messages = new HashMap<>();

        JSONObject messageObject;
        try {
            messageObject = (JSONObject) new JSONParser().parse(new FileReader(f));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }

        messageObject.keySet().forEach(messageName -> {
            messages.put((String) messageName, new Message(readFromValue(messageObject.get(messageName))));
        });
        return messages;
    }

    @SuppressWarnings("unchecked")
    Map<MessageType, Object> readFromValue(Object jsonObject) {
        if (jsonObject instanceof String) {
            return new Pair<MessageType, Object>(MessageType.TEXT, jsonObject).toMap();
        } else if (jsonObject instanceof JSONArray) {
            ArrayList messages = (ArrayList) jsonObject;
            Map<MessageType, Object> map = new HashMap<>();
            map.put(MessageType.TEXT, messages);
            return map;
        } else if (jsonObject instanceof JSONObject) {
            JSONObject j = (JSONObject) jsonObject;
            Map<MessageType, Object> map = new HashMap<>();
            for (String s : (Set<String>) j.keySet()) {
                MessageType messageType = MessageType.valueOf(s.toUpperCase());
                if (messageType == MessageType.TEXT) {
                    map.put(messageType, j.get(s));
                } else if (messageType == MessageType.JSON_TEXT) {
                    Object jsonObj = j.get(s);
                    if (jsonObj instanceof JSONObject) {
                        map.put(messageType, JSONObject.toJSONString((Map) jsonObj));
                    }
                } else if (messageType == MessageType.ACTION_BAR) {
                    Object message = j.get(s);
                    if (!(message instanceof String)) {
                        continue;
                    }
                    map.put(messageType, message);
                } else if (messageType == MessageType.TITLE) {
                    Object jsonArray = j.get(s);
                    if (jsonArray instanceof JSONArray) {
                        map.put(messageType, jsonArray);
                    }
                } else if (messageType == MessageType.SOUND) {
                    Object jsonArray = j.get(s);
                    if (jsonArray instanceof JSONArray) {
                        List soundDescription = (List) jsonArray;
                        List<Sound> sounds = new ArrayList<>();
                        if (!soundDescription.isEmpty() && soundDescription.get(0) instanceof JSONObject) {
                            for (Object so : soundDescription) {
                                if (so instanceof JSONObject) {
                                    JSONObject ob = (JSONObject) so;
                                    String sound_name = (String) ob.get("SOUND_NAME");
                                    float volume = ((Double) ob.get("VOLUME")).floatValue(), pitch = ((Double) ob.get("PITCH")).floatValue();
                                    sounds.add(new Sound(sound_name, pitch, volume));
                                }
                            }
                        }
                        map.put(messageType, sounds);
                    }
                }
            }
            return map;
        }
        throw new IllegalArgumentException("x");
    }

    public Message getMessage(String messageName) {
        if (!messages.containsKey(messageName)) {
            System.out.println("NO MESSAGE FOR " + messageName);
            return new Message("");
        }
        return messages.get(messageName).newInstance();
    }

    public enum MessageType {
        TEXT,
        JSON_TEXT,
        ACTION_BAR,
        TITLE,
        SOUND
    }

}
