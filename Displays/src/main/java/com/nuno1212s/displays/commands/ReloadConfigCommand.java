package com.nuno1212s.displays.commands;

import com.nuno1212s.displays.DisplayMain;
import com.nuno1212s.main.MainData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadConfigCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("chat.reload")) {
            MainData.getIns().getMessageManager().getMessage("NO_PERMISSION").sendTo(commandSender);
            return true;
        }

        DisplayMain.getIns().getChatManager().loadConfig();
        commandSender.sendMessage(ChatColor.RED + "Reloaded the config successfully");

        return true;
    }
}
