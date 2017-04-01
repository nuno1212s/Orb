package com.nuno1212s.events;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.playermanager.PlayerManager;

/**
 * Handles the players leaving the server
 */
public class QuitEvent implements Listener {

	@EventHandler
	public void onDisconnect(PlayerDisconnectEvent e) {
		PlayerData player = PlayerManager.getIns().getPlayer(e.getPlayer().getUniqueId());

		if (player != null) {
			PlayerManager.getIns().removePlayer(player);
		}
	}

}
