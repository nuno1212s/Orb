package com.nuno1212s.core.commandmanager;

import java.util.ArrayList;
import java.util.List;

import com.nuno1212s.core.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ping implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
		if (!(commandSender instanceof Player)) {
			commandSender.sendMessage(ChatColor.RED + "You must be a player to perform this command");
			return true;
		}

		Player p = (Player) commandSender;



			if (args.length == 1) {

				Player player = getOnlinePlayer(args[0]);
				if (player == null) {

					Main.getIns().getMessages().getMessage("PlayerNotFound").sendTo(p);
					return true;
				}

				Main.getIns().getMessages().getMessage("PingOther")
						.format("{PLAYER}", player.getName())
						.format("{PING}", "" + getPing(player))
						.sendTo(p);

				return true;

			}

			Main.getIns().getMessages().getMessage("PingSelf")
					.format("{PLAYER}", p.getName())
					.format("{PING}", "" + getPing(p))
					.sendTo(p);

			return true;
	}

	public int getPing(Player player) {
		return ((CraftPlayer) player).getHandle().ping;
	}

	public static Player getOnlinePlayer(String name) {
		List<String> pOnline = new ArrayList<String>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getName().toLowerCase().contains(name.toLowerCase())) {
				pOnline.add(player.getName());
			}
		}
		if (pOnline.size() == 1) {
			return Bukkit.getPlayer(pOnline.get(0));
		} else {
			return null;
		}
	}

}
