package com.nuno1212s.mercado.listeners;

import com.nuno1212s.mercado.main.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles player quit events
 */
public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (Main.getIns().getMarketManager().getChatManager().hasCallback(e.getPlayer().getUniqueId())) {
            Main.getIns().getMarketManager().getChatManager().removeCallback(e.getPlayer().getUniqueId());
        }
    }

}
