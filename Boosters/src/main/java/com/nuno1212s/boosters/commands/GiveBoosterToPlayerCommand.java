package com.nuno1212s.boosters.commands;

import com.nuno1212s.boosters.boosters.Booster;
import com.nuno1212s.boosters.boosters.BoosterType;
import com.nuno1212s.boosters.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.Pair;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.concurrent.TimeUnit;

/**
 * Give a player a booster
 */
public class GiveBoosterToPlayerCommand implements CommandExecutor {

    public String usage() {
        return ChatColor.RED + "/boosters givebooster <player> <durationInMinutes> <multiplier> " +
                "<type> <server (CURRENT for current server, GLOBAL for all servers)>";
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!commandSender.hasPermission("boosters.add")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").send(commandSender);
            return true;
        }

        if (args.length < 6) {
            commandSender.sendMessage(usage());
            return true;
        }

        String playerName = args[1];

        long durationInMillis;
        float multiplier;
        BoosterType bT;
        String serverType;
        String customName;

        try {
            durationInMillis = TimeUnit.MINUTES.toMillis(Long.parseLong(args[2]));
            multiplier = Float.parseFloat(args[3]);
            bT = BoosterType.valueOf(args[4].toUpperCase());

            if (args[5].equalsIgnoreCase("CURRENT")) {
                serverType = MainData.getIns().getServerManager().getServerType();
            } else {
                serverType = args[5];
            }

            StringBuilder builder = new StringBuilder();
            for (int i = 6; i < args.length; i++) {
                builder.append(i == 6 ? "" : " ");
                builder.append(args[i]);
            }

            customName = ChatColor.translateAlternateColorCodes('&', builder.toString());

        } catch (IllegalArgumentException e) {
            commandSender.sendMessage(usage());
            return true;
        }

        MainData.getIns().getScheduler().runTaskAsync(() -> {

            Pair<PlayerData, Boolean> orLoadPlayer = MainData.getIns().getPlayerManager().getOrLoadPlayer(playerName);

            if (orLoadPlayer.getKey() == null) {
                return;
            }

            MainData.getIns().getMessageManager().getMessage("ADDED_BOOSTER").sendTo(commandSender);
            //MainData.getIns().getMessageManager().getMessage("RECEIVED_BOOSTER").sendTo(orLoadPlayer);

            Booster b = Main.getIns().getBoosterManager().createBooster(orLoadPlayer.getKey().getPlayerID(), multiplier, durationInMillis, bT, serverType, customName);

            Main.getIns().getMysqlHandler().saveBooster(b);
            Main.getIns().getBoosterManager().addBooster(b);

        });

        return false;
    }
}
