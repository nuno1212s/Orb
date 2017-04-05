package com.nuno1212s.bungee.motd;

import com.nuno1212s.bungee.main.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.Map;

/**
 * MOTD command
 */
public class MOTDCommand extends Command {

    public MOTDCommand() {
        super("motd", "motd.*", "");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length == 0) {
            //TODO: Send usage
            return;
        }

        String commandName = args[0];

        if (commandName.equalsIgnoreCase("add")) {
            StringBuilder motd = new StringBuilder();

            for (int i = 1; i < args.length; i++) {
                motd.append(args[i]);
            }

            int id = Main.getIns().getMotdManager().addMOTD(motd.toString());
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

        }

    }
}
