package com.nuno1212s.vanish.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.vanish.main.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }

        if (!commandSender.hasPermission("vanish")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);
            return true;
        }

        Player player = (Player) commandSender;
        boolean playerVanished = Main.getIns().getPlayerManager().isPlayerVanished(player.getUniqueId());

        Main.getIns().getPlayerManager().setPlayerVanished(player.getUniqueId(), !playerVanished);
        if (!playerVanished) {
            MainData.getIns().getMessageManager().getMessage("VANISHED").sendTo(player);
            Main.getIns().getVanishManager().vanishPlayer(player);
        } else {
            MainData.getIns().getMessageManager().getMessage("NOT_VANISHED").sendTo(player);
        }

        return true;
    }
}
