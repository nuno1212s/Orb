package com.nuno1212s.hub.messagemanager;

import com.nuno1212s.hub.main.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Set;

public class Messages implements CommandExecutor {

    private static Messages ins;

    public static Messages getIns() {
        return ins;
    }

    private FileConfiguration fc;
    private File f;

    private String prefix = "";
    private HashMap<String, String> messages = new HashMap<>();

    public String getMessage(String key, String def) {
        if (!messages.containsKey(key)) {
            insertMessage(key, def);
        }
        return messages.get(key);
    }

    @SafeVarargs
    public final String formatMessage(String message, AbstractMap.SimpleEntry<String, String>... strings) {
        if (strings.length != 0) {
            for (AbstractMap.SimpleEntry<String, String> string : strings) {
                message = message.replace(string.getKey(), string.getValue());
            }
        }
        return message;
    }

    private void insertMessage(String key, String vaule) {
        fc.set("Messages." + key, vaule);
        try {
            fc.save(f);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        messages.put(key, ChatColor.translateAlternateColorCodes('&', vaule.replace("%prefix%", prefix)));
    }

    public Messages(Main m) {
        ins = this;
        m.saveResource("messages.yml", false);
        this.f = new File(m.getDataFolder(), "messages.yml");
        this.fc = YamlConfiguration.loadConfiguration(this.f);
        loadMessages();
    }

    public void loadMessages() {
        String prefix = fc.getString("Prefix", "");
        ConfigurationSection sec = fc.getConfigurationSection("Messages");
        Set<String> keys = sec.getKeys(false);
        keys.forEach(key -> {
            messages.put(key, ChatColor.translateAlternateColorCodes('&', sec.getString(key).replace("%prefix%", prefix)));
        });
    }

    void reloadMessages() {
        messages = new HashMap<>();
        loadMessages();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.isOp()) {
            reloadMessages();
            commandSender.sendMessage(ChatColor.GOLD + "Reloaded messages");
        }
        return false;
    }

}
