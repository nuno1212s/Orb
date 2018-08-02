package com.nuno1212s.rankup.commands;

import com.google.common.collect.MapMaker;
import com.nuno1212s.main.MainData;
import com.nuno1212s.messagemanager.Message;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.rankup.main.Main;
import com.nuno1212s.rankup.playermanager.RUPlayerData;
import com.nuno1212s.util.Pair;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class KDDPlayerTop implements CommandExecutor {

    private static Map<UUID, Long> usages = new MapMaker().weakKeys().makeMap();

    private static long SPACING = 3000L;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!(commandSender instanceof Player)) {

            commandSender.sendMessage(ChatColor.RED + "This command is only meant for players");

            return true;
        }

        if (usages.containsKey(((Player) commandSender).getUniqueId())) {

            long usageDate = usages.get(((Player) commandSender).getUniqueId());

            if (usageDate + SPACING > System.currentTimeMillis()) {

                MainData.getIns().getMessageManager().getMessage("NETWORK_REQUEST_WAIT")
                        .format("%timeLeft%", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - usageDate))
                        .sendTo(commandSender);

                return true;

            }

        }

        usages.put(((Player) commandSender).getUniqueId(), System.currentTimeMillis());

        Main.getIns().getMysql().getKDDTop(10).thenAccept((top) -> {

            List<PlayerData> players = MainData.getIns().getPlayerManager().getPlayers();

            for (PlayerData p1 : players) {
                if (p1 instanceof RUPlayerData) {
                    top.put(p1.getPlayerID(), ((RUPlayerData) p1).getKills() - ((RUPlayerData) p1).getDeaths());
                }
            }

            List<Map.Entry<UUID, Integer>> list =
                    new LinkedList<>(top.entrySet());

            list.sort(Map.Entry.comparingByValue());

            Collections.reverse(list);

            Message kdd_top = MainData.getIns().getMessageManager().getMessage("KDD_TOP");

            for (int i = 0; i < 10; i ++) {
                if (list.size() <= i) break;

                Map.Entry<UUID, Integer> uuidIntegerEntry = list.get(i);

                Pair<PlayerData, Boolean> orLoadPlayer = MainData.getIns().getPlayerManager().getOrLoadPlayer(uuidIntegerEntry.getKey());

                kdd_top.format("%player" + (i + 1) + "%", orLoadPlayer.getKey().getPlayerName());
                kdd_top.format("%score" + (i + 1) + "%", uuidIntegerEntry.getValue());
            }

            kdd_top.sendTo(commandSender);
        });

        return true;
    }
}
