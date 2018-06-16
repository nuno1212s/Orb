package com.nuno1212s.warps.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.warps.homemanager.Home;
import com.nuno1212s.warps.main.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Delete a home
 */
public class DelHomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }

        Player player = (Player) commandSender;

        if (args.length >= 1) {

            Home h = Main.getIns().getHomeManager().getPlayerHomeWithName(player.getUniqueId(), args[0]);

            if (h == null) {
                MainData.getIns().getMessageManager().getMessage("NO_HOME_WITH_THAT_NAME").sendTo(player);
                return true;
            }

            List<Home> playerHomes = Main.getIns().getHomeManager().getPlayerHomes(player.getUniqueId());
            playerHomes.remove(h);

            Main.getIns().getHomeManager().registerPlayerHomes(player.getUniqueId(), playerHomes);
            MainData.getIns().getMessageManager().getMessage("DELETED_HOME").sendTo(player);

        } else {
            player.sendMessage(ChatColor.RED + "/delhome <home>");
        }

        return true;
    }
}
