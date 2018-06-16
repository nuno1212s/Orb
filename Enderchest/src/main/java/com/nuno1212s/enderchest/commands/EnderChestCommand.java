package com.nuno1212s.enderchest.commands;

import com.nuno1212s.enderchest.main.Main;
import com.nuno1212s.enderchest.playerdata.EnderChestData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnderChestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "This command is player only");
            return true;
        }

        PlayerData player = MainData.getIns().getPlayerManager().getPlayer(((Player) commandSender).getUniqueId());

        if (player instanceof EnderChestData) {
            EnderChestData data = (EnderChestData) player;

            ((Player) commandSender).openInventory(Main.getIns().getEnderChestManager().getEnderChestFor((Player) commandSender, data));
        }

        return true;
    }
}
