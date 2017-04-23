package com.nuno1212s.messagemanager.messagetypes;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public interface IMessage {

    void sendTo(Map<String, String> formatting, CommandSender... sender);

    static String formatMessage(String message, Map<String, String> formatss) {
        for (Map.Entry<String, String> formats : formatss.entrySet()) {
            message = message.replace(formats.getKey(), formats.getValue());
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        return message;
    }

}
