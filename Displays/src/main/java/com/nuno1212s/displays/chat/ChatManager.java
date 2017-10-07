package com.nuno1212s.displays.chat;

import com.nuno1212s.modulemanager.Module;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Chat manager
 */
public class ChatManager {

    @Getter
    @Setter
    private boolean chatActivated = true;

    @Getter
    private boolean localChatActivated = true;

    @Getter
    private String separator = " » ";

    @Getter
    private long chatTimerGlobal = 15000, chatTimerLocal = 3000, range = (long) Math.pow(25L, 2);

    public ChatManager(Module m) {
        File file = m.getFile("chatConfig.json", true);

        JSONObject config;

        try (FileReader r = new FileReader(file)) {
            config = (JSONObject) new JSONParser().parse(r);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }

        this.chatActivated = (Boolean) config.getOrDefault("ChatActivated", true);
        this.separator = (String) config.getOrDefault("Separator", " » ");
        this.chatTimerGlobal = (Long) config.getOrDefault("ChatCooldownGlobal", 15) * 1000;
        this.chatTimerLocal = (Long) config.getOrDefault("ChatCooldownLocal", 3) * 1000;
        this.localChatActivated = (Boolean) config.getOrDefault("LocalChatActivated", true);
        this.range = (Long) config.getOrDefault("Range", 25);
        this.range *= this.range;
    }

}
