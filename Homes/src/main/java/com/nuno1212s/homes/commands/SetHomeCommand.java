package com.nuno1212s.homes.commands;

import com.nuno1212s.homes.homemanager.Home;
import com.nuno1212s.homes.main.Main;
import com.nuno1212s.main.MainData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Set home command
 */
public class SetHomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!(commandSender instanceof Player)) {
            return true;
        }

        Player player = (Player) commandSender;

        String homeName;

        if (args.length >= 1) {
            homeName = args[0];
        } else {
            homeName = "home";
        }

        int maxAmountOfHomes = Main.getIns().getHomeManager().getMaxAmountOfHomes(player);

        List<Home> playerHomes = Main.getIns().getHomeManager().getPlayerHomes(player.getUniqueId());

        if (playerHomes.size() >= maxAmountOfHomes) {
            MainData.getIns().getMessageManager().getMessage("EXCEEDED_MAX_HOMES").sendTo(player);
            return true;
        }

        if (Main.getIns().getHomeManager().getPlayerHomeWithName(player.getUniqueId(), homeName) != null ){
            MainData.getIns().getMessageManager().getMessage("HOME_EXISTS").sendTo(player);
            return true;
        }

        Home h = new Home(homeName, player.getLocation().clone());
        playerHomes.add(h);
        MainData.getIns().getMessageManager().getMessage("HOME_SET")
                .format("%homeName%", h.getHomeName()).sendTo(player);
        Main.getIns().getHomeManager().registerPlayerHomes(player.getUniqueId(), playerHomes);

        return true;
    }
}
