package com.nuno1212s.messagemanager.messagetypes;

import com.nuno1212s.main.MainData;
import com.nuno1212s.util.ActionBarAPI;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
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
        } else {

            for (ProxiedPlayer player : (ProxiedPlayer[]) sender) {

                player.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));

            }

        }

    }
}
