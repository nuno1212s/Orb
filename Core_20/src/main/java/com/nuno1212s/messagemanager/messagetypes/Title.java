package com.nuno1212s.messagemanager.messagetypes;

import com.nuno1212s.util.TitleAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class Title implements IMessage {

    private final String[] messages = new String[2];

    private int fadeIn, stay, fadeOut;

    public Title(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        this.messages[0] = title;
        this.messages[1] = subTitle;
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
        this.stay = stay;
    }

    @Override
    public void sendTo(Map<String, String> formatting, CommandSender... sender) {

        String title = messages[0];
        String subTitle = messages[1];

        for (Map.Entry<String, String> format : formatting.entrySet()) {
            title = title.replace(format.getKey(), format.getValue());
            subTitle = subTitle.replace(format.getKey(), format.getValue());
        }

        title = ChatColor.translateAlternateColorCodes('&', title);
        subTitle = ChatColor.translateAlternateColorCodes('&', subTitle);

        for (CommandSender commandSender : sender) {
            if (!(commandSender instanceof Player)) {
                continue;
            }
            TitleAPI.sendTitle((Player) commandSender, fadeIn, stay, fadeOut, title, subTitle);
        }

    }
}
