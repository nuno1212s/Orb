package com.nuno1212s.messagemanager.messagetypes;

import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;

import java.util.Map;

/**
 * JSON message
 */
@AllArgsConstructor
public class JSONMessage implements IMessage {

    String jsonMessage;

    @Override
    public void sendTo(Map<String, String> formatting, CommandSender... sender) {

    }
}
