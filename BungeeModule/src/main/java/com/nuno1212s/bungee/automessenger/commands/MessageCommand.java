package com.nuno1212s.bungee.automessenger.commands;

import com.nuno1212s.bungee.loginhandler.SessionData;
import com.nuno1212s.bungee.loginhandler.SessionHandler;
import com.nuno1212s.bungee.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MessageCommand extends Command {

    public MessageCommand() {
        super("automessage");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender instanceof ProxiedPlayer) {
            PlayerData d = MainData.getIns().getPlayerManager().getPlayer(((ProxiedPlayer) commandSender).getUniqueId());

            if (!d.getMainGroup().hasPermission("messages.edit")) {
                return;
            }

            SessionData session = SessionHandler.getIns().getSession(((ProxiedPlayer) commandSender).getUniqueId());
            if (session == null || !session.isAuthenticated()) {
                commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&cNão podes fazer este comando sem estar logado.")));
                return;
            }
        }

        if (args.length == 0) {
            TextComponent textComponent = new TextComponent("/automessage <add/remove/list>");
            textComponent.setColor(ChatColor.RED);
            commandSender.sendMessage(textComponent);
            return;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 2) {
                commandSender.sendMessage(new ComponentBuilder("Insert the message").color(ChatColor.RED).create());
                return;
            }

            StringBuilder message = new StringBuilder();

            for (int i = 1; i < args.length; i++) {
                message.append(args[i]);
                if (i == args.length - 1) {
                    continue;
                }

                message.append(" ");
            }

            List<String> messageSeparated = Arrays.asList(ChatColor.translateAlternateColorCodes('&', message.toString()).split("/n"));

            int i = Main.getIns().getAutoMessageManager().addMessage(messageSeparated);
            commandSender.sendMessage(new ComponentBuilder("Added the message with the ID:").color(ChatColor.GREEN).append(String.valueOf(i)).color(ChatColor.GRAY).create());
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length < 2) {
                commandSender.sendMessage(new ComponentBuilder("/automessage <remove> <id>").color(ChatColor.RED).create());
                return;
            }

            int id;

            try {
                id = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(new ComponentBuilder("Failed to load ID, it must be a number").color(ChatColor.RED).create());
                return;
            }

            if (Main.getIns().getAutoMessageManager().getMessage(id) == null) {
                commandSender.sendMessage(new ComponentBuilder("Message not found").color(ChatColor.RED).create());
                return;
            }

            Main.getIns().getAutoMessageManager().removeMessage(id);
            commandSender.sendMessage(new ComponentBuilder("Message removed").color(ChatColor.GREEN).create());
        } else if (args[0].equalsIgnoreCase("list")) {
            Map<Integer, List<String>> messages = Main.getIns().getAutoMessageManager().getMessages();

            messages.forEach((id, message) -> {
                commandSender.sendMessage(new ComponentBuilder("ID: " + String.valueOf(id)).color(ChatColor.RED).create());
                for (String s : message) {
                    commandSender.sendMessage(TextComponent.fromLegacyText(s));
                }
            });

        }

    }
}
