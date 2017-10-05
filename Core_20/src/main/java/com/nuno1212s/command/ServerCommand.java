package com.nuno1212s.command;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.server_sender.BukkitSender;
import com.nuno1212s.util.Pair;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class ServerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.hasPermission("command.server")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Can only do this when you are a player");
            return true;
        }

        Player p = (Player) commandSender;

        if (args.length < 1) {
            Map<String, Pair<Integer, Integer>> serverPlayerCounts = MainData.getIns().getServerManager().getServerPlayerCounts();
            p.sendMessage(ChatColor.GOLD + "You are currently connected to: " + MainData.getIns().getServerManager().getServerName());

            FancyMessage messageParts = new FancyMessage();
            messageParts.text("You may connect to the following servers: ").color(ChatColor.GOLD);

            serverPlayerCounts.forEach((server, players) -> {
                if (players.value() <= 0) {
                    return;
                }

                messageParts.then();

                FancyMessage toolTip = new FancyMessage(String.valueOf(players.key()) + "/" + String.valueOf(players.value()));
                toolTip.color(ChatColor.GOLD);
                messageParts.text(server + " ,").color(ChatColor.GOLD).formattedTooltip(toolTip).command("/server " + server);

            });

            messageParts.send(p);
        } else {
            String serverName = args[0];

            PlayerData d = MainData.getIns().getPlayerManager().getPlayer(p.getUniqueId());

            BukkitSender.getIns().sendPlayer(d, p, serverName);
        }

        return true;
    }
}
