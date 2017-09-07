package com.nuno1212s.displays.chat;

import com.nuno1212s.displays.Main;
import com.nuno1212s.displays.player.ChatData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handles chat
 */
public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        if (Main.getIns().getChatManager().isChatActivated() || e.getPlayer().hasPermission("chat.override")) {
            PlayerData d = MainData.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());

            if (d instanceof ChatData) {
                long lastUsage = ((ChatData) d).lastLocalChatUsage();
                if (lastUsage + Main.getIns().getChatManager().getChatTimerLocal() > System.currentTimeMillis()
                        && !(e.getPlayer().hasPermission("chat.nocooldown") || e.getPlayer().hasPermission("chat.vipcooldown"))) {
                    MainData.getIns().getMessageManager().getMessage("LOCAL_CHAT_COOLDOWN")
                            .format("%time%", new TimeUtil("SS segundos")
                                    .toTime(lastUsage - System.currentTimeMillis()))
                            .sendTo(e.getPlayer());
                    return;
                }
                ((ChatData) d).setLastLocalChatUsage(System.currentTimeMillis());
            }

            String message = e.getPlayer().hasPermission("chat.color") ? ChatColor.translateAlternateColorCodes('&', e.getMessage()) : e.getMessage();
            String playerName = d.getNameWithPrefix();
            String playerChat = playerName + Main.getIns().getChatManager().getSeparator() + message;

            AtomicBoolean heard = new AtomicBoolean(false);

            Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                        if (player.getUniqueId().equals(e.getPlayer().getUniqueId())) {
                            player.sendMessage(playerChat);
                            return;
                        }
                        if (player.getWorld().getName().equalsIgnoreCase(e.getPlayer().getWorld().getName())) {
                            if (player.getLocation().distanceSquared(e.getPlayer().getLocation()) < Main.getIns().getChatManager().getRange()) {
                                heard.set(true);
                                player.sendMessage(playerChat);
                            }
                        }
                    }
            );

            if (!heard.get()) {
                MainData.getIns().getMessageManager().getMessage("NO_ONE_CLOSE").sendTo(e.getPlayer());
            }
        } else {
            MainData.getIns().getMessageManager().getMessage("CHAT_DISABLED").sendTo(e.getPlayer());
        }
    }

}
