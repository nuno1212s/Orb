    package com.nuno1212s.mercado.listeners;

import com.nuno1212s.mercado.main.Main;
import com.nuno1212s.util.Callback;
import com.nuno1212s.util.Pair;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Handles chat listeners
 */
public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onChat(AsyncPlayerChatEvent e) {
        Callback<Pair<Boolean, Object>> callback = (Callback<Pair<Boolean,Object>>) Main.getIns().getMarketManager().getChatManager().getCallback(e.getPlayer().getUniqueId());
        if (callback != null) {
            e.setCancelled(true);
            Main.getIns().getMarketManager().getChatManager().removeCallback(e.getPlayer().getUniqueId());
            callback.callback(new Pair<>(true, e.getMessage()));
        }
    }

}
