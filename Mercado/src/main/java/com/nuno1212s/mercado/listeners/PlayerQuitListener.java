package com.nuno1212s.mercado.listeners;

import com.nuno1212s.mercado.main.Main;
import com.nuno1212s.mercado.marketmanager.Item;
import com.nuno1212s.mercado.util.chathandlers.ChatHandlerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

/**
 * Handles player quit events
 */
public class PlayerQuitListener implements Listener {

    /**
     * This method handles the players disconnecting before typing the price of the item in the chat,
     * preventing losing items when disconnecting
     *
     * {@link SellInventoryListener#addCallback(UUID, Item)}
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        ChatHandlerManager chatManager = Main.getIns().getMarketManager().getChatManager();
        if (chatManager.hasCallback(e.getPlayer().getUniqueId())) {
            chatManager.getCallback(e.getPlayer().getUniqueId()).callback(false, e.getPlayer());
            chatManager.removeCallback(e.getPlayer().getUniqueId());
        }
    }

}
