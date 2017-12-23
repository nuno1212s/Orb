package com.nuno1212s.rankup.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.permissionmanager.Group;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.rankup.playermanager.RUPlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RGroupCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.hasPermission("groupCommand")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);
            return true;
        }

        if (args.length < 2) {
            commandSender.sendMessage(ChatColor.RED + "/srank <player> <rank>");
            return true;
        }

        RUPlayerData playerData = (RUPlayerData) MainData.getIns().getPlayerManager().getPlayer(args[0]);

        if (playerData == null) {
            commandSender.sendMessage(ChatColor.RED + "Player is not online.");
            return true;
        }

        short groupID;

        try {
            groupID = Short.parseShort(args[1]);
        } catch (NumberFormatException e) {
            commandSender.sendMessage(ChatColor.RED + "Rank must be a number");
            return true;
        }

        Group g = MainData.getIns().getPermissionManager().getGroup(groupID);

        if (g == null) {
            commandSender.sendMessage(ChatColor.RED + "That rank does not exist!");
            return true;
        }

        playerData.setServerRank(groupID, -1);

        MainData.getIns().getMessageManager().getMessage("RANK_UPDATED").format("%rank%", g.getGroupPrefix()).sendTo(playerData);
        commandSender.sendMessage(ChatColor.RED + "Rank has been updated");

        return true;
    }
}
