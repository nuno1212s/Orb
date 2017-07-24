package com.nuno1212s.messagemanager.messagetypes;

import lombok.AllArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * JSON message
 */
@AllArgsConstructor
public class JSONMessage implements IMessage {

    String[] jsonMessage;

    public JSONMessage(String jsonMessage) {
        this.jsonMessage = new String[0];
        this.jsonMessage[0] = jsonMessage;
    }

    public JSONMessage(List<String> jsonMessages) {
        this.jsonMessage = new String[jsonMessages.size()];
        int c = 0;
        for (String message : jsonMessages) {
            this.jsonMessage[c++] = message;
        }
    }

    @Override
    public void sendTo(Map<String, String> formatting, CommandSender... sender) {
        for (String message : jsonMessage) {
            message = IMessage.formatMessage(message, formatting);

            for (CommandSender commandSender : sender) {
                if (commandSender instanceof Player) {
                    ((Player) commandSender).spigot().sendMessage(TextComponent.fromLegacyText(message));
                }
            }
        }
    }
}
