package com.nuno1212s.messages2_0.messagetypes;

import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Created by COMP on 23/04/2017.
 */
@AllArgsConstructor
public class Sound implements IMessage {

    private String soundName;

    private float pitch, volume;

    @Override
    public void sendTo(Map<String, String> formatting, CommandSender... sender) {

        for (CommandSender commandSender : sender) {
            if(!(commandSender instanceof Player)) {
                continue;
            }
            ((Player) commandSender).playSound(((Player) commandSender).getLocation(), soundName, volume, pitch);
        }
    }
}
