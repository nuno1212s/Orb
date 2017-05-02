package com.nuno1212s.displays.chat;

import com.nuno1212s.displays.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Handles chat
 */
public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        if (Main.getIns().getChatManager().isChatActivated() || e.getPlayer().hasPermission("chat.override")) {
            PlayerData d = MainData.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
            String message = e.getPlayer().hasPermission("chat.color") ? ChatColor.translateAlternateColorCodes('&', e.getMessage()) : e.getMessage();
            String playerName = d.getNameWithPrefix();
            String chatPlayer = playerName + Main.getIns().getChatManager().getSeparator() + message;

            Bukkit.getServer().getOnlinePlayers().forEach(player ->
                player.sendMessage(chatPlayer)
            );
        }

    }

}
