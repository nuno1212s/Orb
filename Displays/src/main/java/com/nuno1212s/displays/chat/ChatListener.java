package com.nuno1212s.displays.chat;

import com.nuno1212s.displays.DisplayMain;
import com.nuno1212s.displays.player.ChatData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.punishments.Punishment;
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
        if (DisplayMain.getIns().getChatManager().isChatActivated() || e.getPlayer().hasPermission("chat.override")) {
            PlayerData d = MainData.getIns().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());

            if (d.getPunishment() != null && d.getPunishment().getPunishmentType() == Punishment.PunishmentType.MUTE && !d.getPunishment().hasExpired()) {
                MainData.getIns().getMessageManager().getMessage("MUTED")
                        .format("%time%", d.getPunishment().timeToString()).sendTo(e.getPlayer());
                return;
            }

            if (d instanceof ChatData) {
                if (!((ChatData) d).shouldReceive() && !e.getPlayer().hasPermission("chat.override")) {
                    MainData.getIns().getMessageManager().getMessage("CHAT_DISABLED").sendTo(e.getPlayer());
                    return;
                }

                long lastUsage = ((ChatData) d).lastLocalChatUsage();
                if (lastUsage + DisplayMain.getIns().getChatManager().getChatTimerLocal() > System.currentTimeMillis()
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

            boolean heard;

            if (!DisplayMain.getIns().getChatManager().isLocalChatActivated()) {
                heard = DisplayMain.getIns().getChatManager().sendMessage(message, d, e.getPlayer().getLocation(), true);
            } else {
                heard = DisplayMain.getIns().getChatManager().sendMessage(message, d, e.getPlayer().getLocation(), false);
            }

            if (!heard) {
                MainData.getIns().getMessageManager().getMessage("NO_ONE_CLOSE").sendTo(e.getPlayer());
            }
        } else {
            MainData.getIns().getMessageManager().getMessage("CHAT_DISABLED").sendTo(e.getPlayer());
        }
    }

}
