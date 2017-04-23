package com.nuno1212s.messagemanager.messagetypes;

import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

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
