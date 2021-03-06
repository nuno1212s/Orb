package com.nuno1212s.bungee.motd;

import com.nuno1212s.bungee.loginhandler.SessionData;
import com.nuno1212s.bungee.loginhandler.SessionHandler;
import com.nuno1212s.bungee.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * MOTD command
 */
public class MOTDCommand extends Command {

    public MOTDCommand() {
        super("motd", "motd.*", "");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        if (commandSender instanceof ProxiedPlayer) {
            PlayerData d = MainData.getIns().getPlayerManager().getPlayer(((ProxiedPlayer) commandSender).getUniqueId());

            if (!d.getMainGroup().hasPermission("motd.edit")) {
                return;
            }

            SessionData session = SessionHandler.getIns().getSession(((ProxiedPlayer) commandSender).getUniqueId());
            if (session == null || !session.isAuthenticated()) {
                commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&cNão podes fazer este comando sem estar logado.")));
                return;
            }
        }

        if (args.length == 0) {
            commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "/motd <add/list/remove/addtimer>"));
            return;
        }

        String commandName = args[0];

        if (commandName.equalsIgnoreCase("add")) {
            StringBuilder motd = new StringBuilder();

            for (int i = 1; i < args.length; i++) {
                motd.append(args[i]);
                motd.append(" ");
            }

            String motdText = motd.toString();
            motdText = motdText.replace("%newLine", "\n");
            int id = Main.getIns().getMotdManager().addMOTD(ChatColor.translateAlternateColorCodes('&', motdText));
            commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "MOTD added. ID: " + String.valueOf(id)));
        } else if (commandName.equalsIgnoreCase("remove")) {
            int id;

            if (args.length < 2) {
                return;
            }

            try {
                id = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "ID must be a number"));
                return;
            }

            boolean b = Main.getIns().getMotdManager().removeMOTD(id);

            if (b) {
                commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "MOTD has been deleted"));
            } else {
                commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "MOTD could not be deleted. An MOTD with that ID does not exist"));
            }

        } else if (commandName.equalsIgnoreCase("list")) {

            Map<Integer, String> motds = Main.getIns().getMotdManager().getMotds();

            motds.forEach((motdid, motd) -> {
                commandSender.sendMessage(TextComponent.fromLegacyText("ID: " + String.valueOf(motdid) + ". " + motd));
            });

        } else if (commandName.equalsIgnoreCase("addtimer")) {

            Timer t = new Timer();

            if (args.length < 3) {
                commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "/motd addtimer <timername> <timeinseconds>"));
                return;
            }

            t.setStartTime(System.currentTimeMillis());

            long timeInMillis;

            try {
                int timeInSeconds = Integer.parseInt(args[2]);
                timeInMillis = TimeUnit.SECONDS.toMillis(timeInSeconds);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Time must be a number"));
                return;
            }

            t.setLastTime(timeInMillis);
            t.setTimerSignature("%" + args[1] + "%");

            Main.getIns().getMotdManager().addTimer(t);

            commandSender.sendMessage(TextComponent.fromLegacyText(
                    ChatColor.GREEN + "Timer added. Timer name: " + t.getTimerSignature() + ". Timer time: " + t.toTime()));

        }

    }
}
