package com.nuno1212s.messagemanager.messagetypes;

import com.nuno1212s.main.MainData;

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
                for (org.bukkit.command.CommandSender commandSender : (org.bukkit.command.CommandSender[]) sender) {
                    commandSender.sendMessage(message);
                }
            } else {

                net.md_5.bungee.api.chat.BaseComponent[] baseComponents = net.md_5.bungee.api.chat.TextComponent.fromLegacyText(message);

                for (net.md_5.bungee.api.connection.ProxiedPlayer player : (net.md_5.bungee.api.connection.ProxiedPlayer[]) sender) {

                    player.sendMessage(baseComponents);

                }

            }
        }
    }

    public String toString(Map<String, String> formatting) {

        if (messages.length > 1) {

            StringBuilder builder = new StringBuilder();

            for (String message : this.messages) {
                    builder.append(IMessage.formatMessage(message, formatting));
                    builder.append("\n");

            }

            return builder.toString();
        }

        if (messages.length < 1) {
            return "";
        }

        return IMessage.formatMessage(messages[0], formatting);
    }
}