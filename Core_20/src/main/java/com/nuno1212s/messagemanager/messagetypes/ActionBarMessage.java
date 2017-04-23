package com.nuno1212s.messagemanager.messagetypes;

import com.nuno1212s.util.ActionBarAPI;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

@AllArgsConstructor
public class ActionBarMessage implements IMessage {

    private String message;

    private int durationInSeconds;

    @Override
    public void sendTo(Map<String, String> formatting, CommandSender... sender) {
        String message = IMessage.formatMessage(this.message, formatting);

        for (CommandSender commandSender : sender) {
            if (!(commandSender instanceof Player)) {
                continue;
            }

            if (durationInSeconds != -1) {
                ActionBarAPI.sendActionBar((Player) commandSender, message, durationInSeconds);
            } else {
                ActionBarAPI.sendActionBar((Player) commandSender, message);
            }
        }

    }
}
