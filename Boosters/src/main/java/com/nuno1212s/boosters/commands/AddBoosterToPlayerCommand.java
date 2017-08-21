package com.nuno1212s.boosters.commands;

import com.nuno1212s.boosters.boosters.Booster;
import com.nuno1212s.boosters.boosters.BoosterType;
import com.nuno1212s.boosters.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.CommandUtil.Command;
import com.nuno1212s.util.Pair;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

/**
 * Add a booster to a certain player
 */
public class AddBoosterToPlayerCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{"givebooster", "gtp"};
    }

    @Override
    public String usage() {
        return ChatColor.RED + "/boosters givebooster <player> <durationInMinutes> <multiplier> " +
                "<type> <server (CURRENT for current server, GLOBAL for all servers)>";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("boosters.add")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").send(player);
            return;
        }

        if (args.length < 6) {
            player.sendMessage(usage());
            return;
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

            customName = builder.toString();

        } catch (IllegalArgumentException e) {
            player.sendMessage(usage());
            return;
        }

        MainData.getIns().getScheduler().runTaskAsync(() -> {

            Pair<PlayerData, Boolean> orLoadPlayer = MainData.getIns().getPlayerManager().getOrLoadPlayer(playerName);

            if (orLoadPlayer.getKey() == null) {
                return;
            }

            Booster b = Main.getIns().getBoosterManager().createBooster(orLoadPlayer.getKey().getPlayerID(), multiplier, durationInMillis, bT, serverType, customName);

            Main.getIns().getMysqlHandler().saveBooster(b);
            Main.getIns().getBoosterManager().addBooster(b);

        });

    }
}
