package com.nuno1212s.command;

import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.server_sender.BukkitSender;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class StopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!commandSender.isOp() && !commandSender.hasPermission("stop")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);
            return true;
        }

        BukkitMain.setReady(false);

        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();

        for (Player onlinePlayer : onlinePlayers) {
            BukkitSender.getIns().sendPlayer(MainData.getIns().getPlayerManager().getPlayer(onlinePlayer.getUniqueId()), onlinePlayer, "lobby");
        }

        Bukkit.getScheduler().runTaskLater(BukkitMain.getIns(), () -> {
            Bukkit.getServer().shutdown();
        }, 100);

        return true;
    }
}
