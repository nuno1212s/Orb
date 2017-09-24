package com.nuno1212s.messagemanager.messagetypes;

import com.nuno1212s.main.MainData;
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
    public void sendTo(Map<String, String> formatting, Object... sender) {
        String message = IMessage.formatMessage(this.message, formatting);

        if (!MainData.getIns().isBungee()) {
            for (CommandSender commandSender : (CommandSender[]) sender) {
                if (!(commandSender instanceof Player)) {
                    continue;
                }

                ActionBarAPI actionBarAPI = MainData.getIns().getMessageManager().getActionBarAPI();
                if (durationInSeconds != -1) {
                    actionBarAPI.sendActionBar((Player) commandSender, message, durationInSeconds);
                } else {
                    actionBarAPI.sendActionBar((Player) commandSender, message);
                }
            }
        }

    }
}
