package com.nuno1212s.core.configmanager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.plugin.Plugin;

import lombok.Getter;

public class MainConfig {
	
	@Getter private static MainConfig ins = new MainConfig();
	
	@Getter Config mConfig;

	private File mFile;


	public void load(Plugin plugin) {

	    plugin.saveDefaultConfig();

		this.mFile = new File(plugin.getDataFolder(), "config.yml");
		if (!mFile.exists()) {

			try {
				mFile.createNewFile();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

		this.mConfig = new Config(plugin, "/config.yml");

		this.header = new ArrayList<>();
		this.footer = new ArrayList<>();
		List<String> header = mConfig.getListString("header", Arrays.asList("&r"));
		List<String> footer = mConfig.getListString("footer", Arrays.asList("&r"));
		header.forEach(h ->
				this.header.add(org.bukkit.ChatColor.translateAlternateColorCodes('&', h))
		);
		footer.forEach(f ->
				this.footer.add(org.bukkit.ChatColor.translateAlternateColorCodes('&', f))
		);

	}

	@Getter
	private List<String> header, footer;

}
