package com.nuno1212s.core.permissions;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PermissionsListeners implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		PlayerPermissions.getIns().unregisterPlayer(event.getPlayer());
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {

		if ((event.getAction() == Action.LEFT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_AIR)) {
			return;
		}

		if (!event.getPlayer().hasPermission("novus.core.interact")) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to perform this action.");
		}

	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {

		if (!event.getPlayer().hasPermission("novus.core.build")) {

			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to perform this action.");

		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {

		if (!event.getPlayer().hasPermission("novus.core.build")) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to perform this action.");
		}

	}

}
