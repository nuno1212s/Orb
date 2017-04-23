package com.nuno1212s.messages2_0.messagetypes;

import org.bukkit.command.CommandSender;

import java.util.Map;

/**
 * Created by COMP on 23/04/2017.
 */
public interface IMessage {

    void sendTo(Map<String, String> formatting, CommandSender... sender);

}
