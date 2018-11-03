package com.nuno1212s.messagemanager.messagetypes;

import com.nuno1212s.main.MainData;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

@AllArgsConstructor
public class Sound implements IMessage {

    private String soundName;

    private float pitch, volume;

    @Override
    public void sendTo(Map<String, String> formatting, Object... sender) {
        if (!MainData.getIns().isBungee()) {
            for (CommandSender commandSender : (CommandSender[]) sender) {
                if (!(commandSender instanceof Player)) {
                    continue;
                }

                ((Player) commandSender).playSound(((Player) commandSender).getLocation(), soundName, volume, pitch);
            }
        }
    }
}
