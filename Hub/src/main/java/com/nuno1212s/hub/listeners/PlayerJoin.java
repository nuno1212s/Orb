package com.nuno1212s.hub.listeners;

import com.nuno1212s.core.permissions.PermissionsGroup;
import com.nuno1212s.core.permissions.PlayerPermissions;
import com.nuno1212s.core.playermanager.PlayerManager;
import com.nuno1212s.core.util.LoginEvent;
import com.nuno1212s.core.util.Title;
import com.nuno1212s.hub.main.Main;
import com.nuno1212s.hub.guis.InfoInventory;
import com.nuno1212s.hub.messagemanager.Messages;
import com.nuno1212s.hub.scoreboard.ScoreboardHandler;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.nuno1212s.hub.servermanager.ServerManager;
import com.nuno1212s.hub.utils.SpawnManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.Inventory;

import com.nuno1212s.hub.guis.OptionsInventory;
import com.nuno1212s.hub.guis.ProfileInventory;
import com.nuno1212s.hub.guis.ServerSelectorInventory;
import com.nuno1212s.hub.guis.options.PlayerVisibilityOption;

public class PlayerJoin implements Listener, LoginEvent {
	public Main m;

	public PlayerJoin(Main m) {
		this.m = m;

		File f = new File(m.getDataFolder(), "motd.yml");
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		if (fc.isSet("Motd")) {
			for (String a : fc.getStringList("Motd")) {
				this.motd.add(ChatColor.translateAlternateColorCodes('&', a));
			}
		}

	}

	private List<String> motd = new ArrayList<>();

	public void onLogin(PlayerLoginEvent e) {
		PlayerPermissions.getIns().registerPlayer(e.getPlayer());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();


		e.setJoinMessage(null);

		if (p.hasPermission("novus.core.joinmessage")) {
			String joinMessage = Messages.getIns().getMessage("JoinMessage", "%playerName% &7entrou no server.");
			PermissionsGroup pg = PlayerPermissions.getIns().getGroup(p);
			String name = pg.getPrefix() + p.getDisplayName() + pg.getSuffix();
			joinMessage = Messages.getIns().formatMessage(joinMessage, new AbstractMap.SimpleEntry<>("%playerName%", name));
			e.setJoinMessage(joinMessage);
		}

		loadInventory(p);

		Title.sendFullTitle(p, 5, 30, 5, ChatColor.AQUA.toString() + ChatColor.BOLD + "NOVUS NETWORK", ChatColor.WHITE.toString() + ChatColor.BOLD + "Bem-Vindo!");

		p.teleport(SpawnManager.getIns().spawnLocation);

		ScoreboardHandler.getIns().handlePlayerJoin(e.getPlayer(), PlayerManager.getIns().getPlayerData(e.getPlayer().getUniqueId()));

		sendMotd(p);
	}

	@Override
	public void forceSave(UUID u) {

	}

	private void sendMotd(Player p) {
		for (String a : motd) {
			a = a.replace("{PLAYER}", p.getName());
			a = a.replace("{ONLINE}", "" + ServerManager.getIns().globalOnlinePlayers);
			p.sendMessage(a);
		}
	}

	private void loadInventory(Player p) {
		Inventory inv = p.getInventory();

		inv.clear();

		inv.setItem(ServerSelectorInventory.getIns().slot, ServerSelectorInventory.getIns().item);
		inv.setItem(ProfileInventory.getIns().slot, ProfileInventory.getIns().getItem(p.getName()));
		inv.setItem(OptionsInventory.getIns().slot, OptionsInventory.getIns().item);
		inv.setItem(PlayerVisibilityOption.getIns().slot, PlayerVisibilityOption.getIns().getItem(p));
		inv.setItem(InfoInventory.getIns().slot, InfoInventory.getIns().item);

	}


}
