package com.nuno1212s.npcinbox.listeners;

import com.nuno1212s.npcinbox.main.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles the player disconnecting mid typing the reward messages
 */
public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e)  {
        Main.getIns().getChatManager().unregisterPlayer(e.getPlayer().getUniqueId());
    }

}
