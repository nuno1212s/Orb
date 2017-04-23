package com.nuno1212s.messages2_0.messagetypes;

import com.nuno1212s.util.ActionBarAPI;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Created by COMP on 23/04/2017.
 */
@AllArgsConstructor
public class ActionBarMessage implements IMessage {

    private String message;

    private int durationInSeconds;

    @Override
    public void sendTo(Map<String, String> formatting, CommandSender... sender) {
        String message = this.message;
        for (Map.Entry<String, String> frmt : formatting.entrySet()) {
            message = message.replace(frmt.getKey(), frmt.getValue());
        }

        for (CommandSender commandSender : sender) {
            if (!(commandSender instanceof Player)) {
                continue;
            }

            ActionBarAPI.sendActionBar((Player) commandSender, message, durationInSeconds);
        }

    }
}
