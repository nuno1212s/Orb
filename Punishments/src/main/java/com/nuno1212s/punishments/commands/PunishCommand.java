package com.nuno1212s.punishments.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.punishments.main.Main;
import com.nuno1212s.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PunishCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.hasPermission("punishments")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);
            return true;
        }

        if (args.length < 1) {
            commandSender.sendMessage(ChatColor.RED + "/punish <player>");
            return true;
        }

        String playerName = args[0];

        Player player = Bukkit.getPlayer(playerName);
        UUID playerID;

        if (player != null) {
            playerID = player.getUniqueId();
        } else {
            Pair<PlayerData, Boolean> playerData = MainData.getIns().getPlayerManager().getOrLoadPlayer(playerName);
            if (playerData.key() == null) {
                MainData.getIns().getMessageManager().getMessage("PLAYER_NOT_FOUND").sendTo(commandSender);
                return true;
            }
            playerID = playerData.key().getPlayerID();
        }

        Main.getIns().getInventoryManager().setPlayerToTarget(((Player) commandSender).getUniqueId(), playerID);
        ((Player) commandSender).openInventory(Main.getIns().getInventoryManager().getMainInventory());

        return true;
    }
}
