package com.nuno1212s.bungee.commands;

import com.nuno1212s.main.MainData;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class AlertCommand extends Command {

    public AlertCommand() {
        super("alert", "alert", "say");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        StringBuilder messages = new StringBuilder();

        for (String arg : args) {
            messages.append(arg);
            messages.append(" ");
        }

        ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(MainData.getIns().getMessageManager().getMessage("ALERT")
                .format("%message%", messages.toString()).toString()));

    }
}
