package com.nuno1212s.messages2_0;

import com.nuno1212s.messagemanager.*;
import com.nuno1212s.messages2_0.messagetypes.IMessage;
import com.nuno1212s.messages2_0.messagetypes.StringMessage;
import com.nuno1212s.util.ActionBarAPI;
import com.nuno1212s.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Load messages
 */
public class Messages {

    private Map<String, Message> messages;

    private List<File> registeredFiles;

    public Messages(File j) {
        new ActionBarAPI();
        this.registeredFiles = new ArrayList<>();
        this.registeredFiles.add(j);
        reloadMessages();
    }

    public void addMessageFile(File messageFile) {
        this.registeredFiles.add(messageFile);
        reloadMessages();
    }

    public void reloadMessages() {

        this.messages = new HashMap<>();

        registeredFiles.forEach(f -> {
            JSONObject messageObject;
            try {
                messageObject = (JSONObject) new JSONParser().parse(new FileReader(f));
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                return;
            }


            messageObject.keySet().forEach(messageName -> {
                messages.put((String) messageName, new Message(readFromValue(messageObject.get(messageName))));
            });
        });

    }

    @SuppressWarnings("unchecked")
    List<IMessage> readFromValue(Object jsonObject) {
        List<IMessage> messages = new ArrayList<>();
        if (jsonObject instanceof String) {
            messages.add(new StringMessage((String) jsonObject));
        } else if (jsonObject instanceof List) {
            if (((List) jsonObject).get(0) instanceof String) {
                messages.add(new StringMessage((List<String>) jsonObject));
            }
        } else if (jsonObject instanceof JSONObject) {
            JSONObject j = (JSONObject) jsonObject;
        }
        return messages;
    }

    public Message getMessage(String messageName) {
        if (!messages.containsKey(messageName)) {
            System.out.println("NO MESSAGE FOR " + messageName);
            return new Message();
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
