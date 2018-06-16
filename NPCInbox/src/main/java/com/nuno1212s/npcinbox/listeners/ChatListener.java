package com.nuno1212s.npcinbox.listeners;

import com.nuno1212s.npcinbox.chat.MessageBuilder;
import com.nuno1212s.npcinbox.main.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Listens in on the chat
 */
public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
    public void onChat(AsyncPlayerChatEvent e) {

        MessageBuilder playerMessageBuilder = Main.getIns().getChatManager().getPlayerMessageBuilder(e.getPlayer().getUniqueId());
        if (playerMessageBuilder != null) {
            e.setCancelled(true);
            playerMessageBuilder.append(e.getMessage());
        }

    }

}
