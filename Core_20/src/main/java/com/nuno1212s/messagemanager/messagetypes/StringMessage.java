package com.nuno1212s.messagemanager.messagetypes;

import com.nuno1212s.main.MainData;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

/**
 * String message
 */
public class StringMessage implements IMessage {

    private String[] messages;

    public StringMessage(String... messages) {
        this.messages = messages;
    }

    public StringMessage(List<String> messages) {
        this.messages = new String[messages.size()];
        int i = 0;
        for (String message : messages) {
            this.messages[i++] = message;
        }
    }

    @Override
    public void sendTo(Map<String, String> formatting, Object... sender) {
        for (String message : messages) {
            message = IMessage.formatMessage(message, formatting);

            if (!MainData.getIns().isBungee()) {
                for (CommandSender commandSender : (CommandSender[]) sender) {
                    commandSender.sendMessage(message);
                }
            }
        }
    }

    public String toString(Map<String, String> formatting) {

        if (messages.length < 1) {
            return "";
        }

        return IMessage.formatMessage(messages[0], formatting);
    }
}