package com.nuno1212s.displays.chat;

import com.nuno1212s.displays.Main;
import com.nuno1212s.displays.player.ChatData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Global chat command
 */
public class GlobalChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!(commandSender instanceof Player)) {
            return true;
        }

        PlayerData data = MainData.getIns().getPlayerManager().getPlayer(((Player) commandSender).getUniqueId());

        if (data instanceof ChatData) {
            long lastUsage = ((ChatData) data).lastGlobalChatUsage();
            if (lastUsage + Main.getIns().getChatManager().getChatTimerGlobal() > System.currentTimeMillis()
                    && !commandSender.hasPermission("chat.nocooldown")) {
                MainData.getIns().getMessageManager().getMessage("GLOBAL_CHAT_COOLDOWN")
                        .format("%time%", new TimeUtil("SS segundos")
                                .toTime(lastUsage - System.currentTimeMillis()))
                        .sendTo(commandSender);
                return true;
            }
            ((ChatData) data).setLastGlobalChatUsage(System.currentTimeMillis());
        }

        StringBuilder message = new StringBuilder();

        for (String arg : args) {
            message.append(arg);
            message.append(" ");
        }

        String finalMessage = commandSender.hasPermission("chat.color") ? ChatColor.translateAlternateColorCodes('&', message.toString()) : message.toString();
        String playerChat = data.getNameWithPrefix() + Main.getIns().getChatManager().getSeparator() + finalMessage;


        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendMessage(playerChat);
        });

        return true;
    }
}
