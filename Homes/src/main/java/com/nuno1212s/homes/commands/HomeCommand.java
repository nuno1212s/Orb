package com.nuno1212s.homes.commands;

import com.nuno1212s.homes.homemanager.Home;
import com.nuno1212s.homes.main.Main;
import com.nuno1212s.main.MainData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

/**
 * Home commands
 */
public class HomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }

        String homeName;

        Player player = (Player) commandSender;

        if (args.length < 1) {

            List<Home> playerHomes = Main.getIns().getHomeManager().getPlayerHomes(player.getUniqueId());

            if (playerHomes.size() > 1) {
                String homes = getHomes(Main.getIns().getHomeManager().getPlayerHomes(player.getUniqueId()));
                MainData.getIns().getMessageManager().getMessage("SPECIFY_HOME_NAME").sendTo(player);
                MainData.getIns().getMessageManager().getMessage("PLAYER_HOMES").format("%homes%", homes);
            } else if (playerHomes.size() == 1) {
                Home home = playerHomes.get(0);

                player.teleport(home.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                MainData.getIns().getMessageManager().getMessage("TELEPORTED_HOME").sendTo(player);
            } else {
                MainData.getIns().getMessageManager().getMessage("NO_HOMES").sendTo(player);
            }

        } else {
            homeName = args[1];

            Home playerHomeWithName = Main.getIns().getHomeManager().getPlayerHomeWithName(player.getUniqueId(), homeName);

            if (playerHomeWithName == null) {
                String homes = getHomes(Main.getIns().getHomeManager().getPlayerHomes(player.getUniqueId()));
                MainData.getIns().getMessageManager().getMessage("NO_HOME_WITH_THAT_NAME").sendTo(player);
                MainData.getIns().getMessageManager().getMessage("PLAYER_HOMES").format("%homes%", homes);
                return true;
            }

            player.teleport(playerHomeWithName.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            MainData.getIns().getMessageManager().getMessage("TELEPORTED_HOME").sendTo(player);

        }

        return true;
    }

    private String getHomes(List<Home> homes) {
        StringBuilder homeBuilder = new StringBuilder("");

        boolean first = true;

        for (Home home : homes) {
            if (first) {
                first = false;
                homeBuilder.append(home.getHomeName());
                continue;
            }

            homeBuilder.append(", ");
            homeBuilder.append(home.getHomeName());
        }

        return homeBuilder.toString();
    }

}
