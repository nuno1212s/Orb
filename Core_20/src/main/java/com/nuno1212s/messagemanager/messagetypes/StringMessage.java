package com.nuno1212s.messagemanager.messagetypes;

import org.bukkit.ChatColor;
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
    public void sendTo(Map<String, String> formatting, CommandSender... sender) {
        for (String message : messages) {
            message = IMessage.formatMessage(message, formatting);

            for (CommandSender commandSender : sender) {
                commandSender.sendMessage(message);
            }
        }
    }

    public String toString(Map<String, String> formatting) {
        return ChatColor.translateAlternateColorCodes('&', IMessage.formatMessage(messages[0], formatting));
    }
}