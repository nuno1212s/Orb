package com.nuno1212s.hub.servermanager;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.nuno1212s.core.mysql.MySqlDB;
import com.nuno1212s.core.serverstatus.ServerInfo;
import com.nuno1212s.core.serverstatus.ServerStatus;
import com.nuno1212s.core.serverstatus.Status;
import com.nuno1212s.hub.main.Main;
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import com.nuno1212s.hub.guis.ServerSelectorInventory;
import com.nuno1212s.hub.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.*;

/**
 * Manages the servers
 */
public class ServerManager {

    @Getter
    private static ServerManager ins;
    private File f;
    public FileConfiguration fc;

    public final List<NovusServer> servers;
    public HashMap<Status, String> status = new HashMap<>();

    private HashMap<String, Location> locations;
    private HashMap<String, NPC> npcs;
    private HashMap<String, Hologram> holograms;

    public int globalOnlinePlayers;

    public ServerManager(Main m) {
        ins = this;

        this.f = new File(m.getDataFolder(), "servers.yml");
        if (!f.exists()) {
            m.saveResource("servers.yml", false);
        }
        fc = YamlConfiguration.loadConfiguration(this.f);

        this.status.put(Status.OFFLINE, fc.getString("Status.Offline", "&cOffline!"));
        this.status.put(Status.ONLINE, fc.getString("Status.Online", "&e{ONLINE} Playing"));

        servers = Collections.synchronizedList(new ArrayList<>());
        npcs = new HashMap<>();
        holograms = new HashMap<>();
        locations = new HashMap<>();

        ConfigurationSection servers = fc.getConfigurationSection("Servers");
        Set<String> keys = servers.getKeys(false);
        for (String key : keys) {
            ConfigurationSection sec = servers.getConfigurationSection(key);
            NovusServer ns = null;
            try {
                ns = new NovusServer(ServerStatus.getIns().getStatusFromServer(sec.getString("BungeeId")), ChatColor.translateAlternateColorCodes('&', sec.getString("Name")), key);
            } catch (NullPointerException ex) {
                System.out.println("Server with key '" + key + "' not found.");
                continue;
            }
            this.servers.add(ns);
            if (ConfigUtils.getIns().getLocation("Servers." + key + ".NpcLocation", fc) != null) {
                this.locations.put(key, ConfigUtils.getIns().getLocation("Servers." + key + ".NpcLocation", fc));
            }

        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), this::updateServers, 20L, 20L);

    }

    public void saveConfig() {
        try {
            fc.save(f);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public NovusServer getServerByBungeeID(String bungeeId) {
        synchronized (servers) {
            for (NovusServer s : this.servers) {
                if (s.getInfo().getServerName().equalsIgnoreCase(bungeeId)) {
                    return s;
                }
            }
        }
        return null;
    }

    public void unload() {
        synchronized (servers) {
            for (NovusServer s : this.servers) {
                s.getH().delete();
                s.getNpc().destroy();
            }
        }
    }

    private void updateEntities() {
        synchronized (servers) {
            for (NovusServer s : this.servers) {
                if (!locations.containsKey(s.getConfiguratioName())) {
                    continue;
                }
                if (s.getNpc() == null) {
                    NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, ChatColor.RESET + "");
                    npc.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA, fc.getString("Servers." + s.getConfiguratioName() + ".Skin", ""));
                    npc.data().setPersistent(NPC.PLAYER_SKIN_USE_LATEST, false);
                    npc.data().set("Connect", s.getInfo().getServerName());
                    npc.spawn(locations.get(s.getConfiguratioName()));
                    s.setNpc(npc);
                }
                if (s.getH() == null) {
                    Hologram h = HologramsAPI.createHologram(Main.getInstance(), locations.get(s.getConfiguratioName()).clone().add(0, 2.6, 0));
                    h.insertTextLine(0, ChatColor.translateAlternateColorCodes('&', s.getDisplayName()));
                    h.insertTextLine(1, s.getStatus());
                    s.setH(h);
                } else {
                    s.getH().removeLine(1);
                    s.getH().insertTextLine(1, s.getStatus());
                }
            }
        }

        if (ServerSelectorInventory.getIns() != null)
            ServerSelectorInventory.getIns().updateMenu();
    }

    private void updateServers() {
        globalOnlinePlayers = 0;

        List<ServerInfo> allInfo = MySqlDB.getIns().getAllInfo();
        synchronized (servers) {
            servers.forEach(server -> {
                for (ServerInfo serverInfo : allInfo) {
                    if (serverInfo.getServerName().equalsIgnoreCase(server.getInfo().getServerName())) {
                        server.getInfo().update(serverInfo);
                        globalOnlinePlayers = globalOnlinePlayers + serverInfo.getCurrentPlayers();
                        break;
                    }
                }
            });
        }

        Bukkit.getScheduler().runTask(Main.getInstance(), this::updateEntities);
    }

}
