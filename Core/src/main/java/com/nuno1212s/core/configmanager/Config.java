package com.nuno1212s.core.configmanager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Config {

	private boolean allowsave;
	private FileConfiguration fconfig;
	private String path;

	public Config(Plugin p, String path) {
		this(p.getDataFolder() + path);
	}

	public Config(String path) {
		this.path = path;
		createFile();
		fconfig = new YamlConfiguration();
		allowsave = false;
		load();
	}

	public void load() {
		createFile();
		try {
			fconfig.load(path);
			allowsave = true;
		} catch (Exception e) {
			e.printStackTrace();
			allowsave = false;
		}
	}

	public void save() {
		createFile();
		try {
			if (allowsave) {
				fconfig.save(path);
			} else {
				throw new IllegalStateException("config failed to load; save not allowed");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createFile() {
		File file = new File(path);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String getString(String key, String def) {
		if (fconfig.contains(key)) {
			return fconfig.getString(key);
		} else {
			fconfig.set(key, def);
			save();
			return def;
		}
	}

	public int getInt(String key, int def) {
		if (fconfig.contains(key)) {
			return fconfig.getInt(key);
		} else {
			fconfig.set(key, def);
			save();
			return def;
		}
	}

	public long getLong(String key, long def) {
		if (fconfig.contains(key)) {
			return fconfig.getInt(key);
		} else {
			fconfig.set(key, def);
			save();
			return def;
		}
	}

	public boolean getBoolean(String key, boolean def) {
		if (fconfig.contains(key)) {
			return fconfig.getBoolean(key);
		} else {
			fconfig.set(key, def);
			save();
			return def;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T extends Enum> T getEnumValue(String key, T def) {
		if (fconfig.contains(key)) {
			String s = getString(key, def.name());
			T[] constants = (T[]) def.getDeclaringClass().getEnumConstants();
			for (T constant : constants) {
				if (constant.name().equalsIgnoreCase(s)) {
					return constant;
				}
			}
			return def;
		} else {
			load();
			fconfig.set(key, def.name());
			save();
			return def;
		}
	}

	public List<String> getListString(String key, List<String> def) {
		if (fconfig.contains(key)) {
			return fconfig.getStringList(key);
		} else {
			fconfig.set(key, def);
			save();
			return def;
		}
	}

	public double getDouble(String key, double def) {
		if (fconfig.contains(key)) {
			return fconfig.getDouble(key);
		} else {
			fconfig.set(key, def);
			save();
			return def;
		}
	}

	public Location getLocation(String key) {

		String t = fconfig.getString(key);
		if (t == null) {
			return null;
		}
		t = t.substring(1, t.length() - 1);

		String[] args = t.split(", ");
		String world = args[0];

		double x = 0.0;
		double y = 0.0;
		double z = 0.0;
		double yaw = 0.0;
		double pitch = 0.0;

		try {

			x = Double.parseDouble(args[1]);
			y = Double.parseDouble(args[2]);
			z = Double.parseDouble(args[3]);
			yaw = Double.parseDouble(args[4]);
			pitch = Double.parseDouble(args[5]);

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

		return new Location(Bukkit.getWorld(world), x, y, z, (float) yaw, (float) pitch);
	}

	public void setLocation(String key, Location l) {
		String location = "(" + l.getWorld().getName() + ", " + l.getX() + ", " + l.getY() + ", " + l.getZ() + ", "
				+ l.getYaw() + ", " + l.getPitch() + ")";
		fconfig.set(key, location);
	}

	public void setLocationAndSave(String key, Location l) {
		String location = "(" + l.getWorld().getName() + ", " + l.getX() + ", " + l.getY() + ", " + l.getZ() + ", "
				+ l.getYaw() + ", " + l.getPitch() + ")";
		fconfig.set(key, location);
		save();
	}

	public void setString(String key, String val) {
		fconfig.set(key, val);
	}

	public void setStringAndSave(String key, String val) {
		fconfig.set(key, val);
		save();
	}

	public void setInt(String key, int val) {
		fconfig.set(key, val);
	}

	public void setIntAndSave(String key, int val) {
		fconfig.set(key, val);
		save();
	}

	public void setLong(String key, long val) {
		fconfig.set(key, val);
	}

	public void setLongAndSave(String key, long val) {
		fconfig.set(key, val);
		save();
	}

	public void setBool(String key, boolean val) {
		fconfig.set(key, val);
	}

	public void setBoolAndSave(String key, boolean val) {
		fconfig.set(key, val);
		save();
	}

	@SuppressWarnings("rawtypes")
	public <T extends Enum> void setEnumValue(String key, T val) {
		fconfig.set(key, val.name());
	}

	@SuppressWarnings("rawtypes")
	public <T extends Enum> void setEnumAndSave(String key, T val) {
		fconfig.set(key, val.name());
		save();
	}

	public void setListString(String key, List<String> val) {
		fconfig.set(key, val);
	}

	public void setListStringAndSave(String key, List<String> val) {
		fconfig.set(key, val);
		save();
	}

	public List<String> getDefaultSubNotes() {
		List<String> ret = new ArrayList<>();
		try {
			for (Object o : fconfig.getDefaultSection().getKeys(false).toArray()) {
				ret.add((String) o);
			}
		} catch (Exception e) {
		}
		return ret;
	}

	public List<String> getSubNodes(String node) {
		List<String> ret = new ArrayList<>();
		try {
			for (Object o : fconfig.getConfigurationSection(node).getKeys(false).toArray()) {
				ret.add((String) o);
			}
		} catch (Exception e) {
		}
		return ret;
	}

	public void deleteNode(String node) {
		fconfig.set(node, null);
		save();
	}

	public boolean keyExists(String node) {
		return fconfig.contains(node);
	}

	public ConfigurationSection getSection(String key) {
		return fconfig.getConfigurationSection(key);
	}

}
