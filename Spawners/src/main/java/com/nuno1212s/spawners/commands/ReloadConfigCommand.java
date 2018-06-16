package com.nuno1212s.spawners.commands;

import com.nuno1212s.main.MainData;
import com.nuno1212s.spawners.main.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadConfigCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.hasPermission("spawners.reload")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);
            return true;
        }

        Main.getIns().getRewardManager().load();
        Main.getIns().getEntityManager().loadLootingConfig();
        Main.getIns().getEntityManager().loadDrops();
        commandSender.sendMessage(ChatColor.GREEN + "Reloaded the config.!");

        return true;
    }
}
