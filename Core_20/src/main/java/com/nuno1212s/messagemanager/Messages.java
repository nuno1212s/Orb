package com.nuno1212s.messagemanager;

import com.nuno1212s.messagemanager.messagetypes.*;
import com.nuno1212s.util.ActionBarAPI;
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
    private List<IMessage> readFromValue(Object jsonObject) {
        List<IMessage> messages = new ArrayList<>();
        if (jsonObject instanceof String) {
            messages.add(new StringMessage((String) jsonObject));
        } else if (jsonObject instanceof List) {
            if (((List) jsonObject).get(0) instanceof String) {
                messages.add(new StringMessage((List<String>) jsonObject));
            }
        } else if (jsonObject instanceof JSONObject) {
            JSONObject j = (JSONObject) jsonObject;
            for (String s : (Set<String>) j.keySet()) {
                Messages.MessageType messageType = Messages.MessageType.valueOf(s.toUpperCase());
                if (messageType == MessageType.TEXT) {
                    messages.add(new StringMessage((String) j.get(s)));
                } else if (messageType == MessageType.JSON_TEXT) {
                    Object jsonObj = j.get(s);
                    if (jsonObj instanceof JSONObject) {
                        messages.add(new JSONMessage(JSONObject.toJSONString((Map) jsonObj)));
                    }
                } else if (messageType == MessageType.ACTION_BAR) {
                    Object message = j.get(s);
                    if (message instanceof String) {
                        messages.add(new ActionBarMessage((String) message
                                , -1));
                    } else if (message instanceof JSONObject) {
                        Object message1 = ((JSONObject) message).get("Message");
                        Object duration = ((JSONObject) message).get("Duration");
                        if (duration == null) {
                            duration = -1;
                        }
                        messages.add(new ActionBarMessage((String) message1,
                                ((Long) duration).intValue()));
                    }
                } else if (messageType == MessageType.TITLE) {
                    Object message = j.get(s);
                    if (message instanceof JSONArray) {
                        List<String> jsonArrays = (JSONArray) message;
                        messages.add(new Title(jsonArrays.get(0), jsonArrays.get(1)
                                , 20, 50, 20));
                    } else if (message instanceof JSONObject) {
                        JSONObject object = (JSONObject) message;
                        String title = (String) object.get("Title");
                        String subTitle = (String) object.get("SubTitle");
                        int fadeIn = ((Long) object.get("FadeIn")).intValue(),
                                fadeOut = ((Long) object.get("FadeOut")).intValue(),
                                stay = ((Long) object.get("Stay")).intValue();
                        messages.add(new Title(title, subTitle, fadeIn, stay, fadeOut));
                    }
                } else if (messageType == MessageType.SOUND) {
                    Object jsonArray = j.get(s);
                    if (jsonArray instanceof JSONArray) {
                        List soundDescription = (List) jsonArray;
                        if (!soundDescription.isEmpty() && soundDescription.get(0) instanceof JSONObject) {
                            for (Object so : soundDescription) {
                                if (so instanceof JSONObject) {
                                    JSONObject ob = (JSONObject) so;
                                    String sound_name = (String) ob.get("SOUND_NAME");
                                    float volume = ((Double) ob.get("VOLUME")).floatValue(), pitch = ((Double) ob.get("PITCH")).floatValue();
                                    messages.add(new Sound(sound_name, pitch, volume));
                                }
                            }
                        }
                    }
                }
            }
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
