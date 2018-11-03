package com.nuno1212s.messagemanager.messagetypes;

import net.md_5.bungee.api.ChatColor;

import java.util.Map;

public interface IMessage {

    void sendTo(Map<String, String> formatting, Object... receiver);

    static String formatMessage(String message, Map<String, String> formatss) {
        for (Map.Entry<String, String> formats : formatss.entrySet()) {
            message = message.replace(formats.getKey(), formats.getValue());
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        return message;
    }

}
