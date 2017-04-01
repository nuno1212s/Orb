package com.nuno1212s.core.serverstatus;

import lombok.Getter;
import lombok.Setter;
import com.nuno1212s.core.main.Main;
import com.nuno1212s.core.mysql.MySqlDB;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;

/**
 * Server status
 */
public class ServerStatus implements Listener {

    @Getter
    private static ServerStatus ins;

    private ServerInfo serverInfo;

    @Getter
    @Setter
    public boolean canSave;

    public ServerStatus() {
        ins = this;
        serverInfo = new ServerInfo(Main.getInstance().getConfig().getString("ServerName"), 0, Bukkit.getServer().getMaxPlayers(), Status.ONLINE);
        canSave = Main.getInstance().getConfig().getBoolean("CanSave", false);
        if (canSave) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                MySqlDB.getIns().updateServerInformation(serverInfo);
            });
        }
    }

    public ServerInfo getStatusFromServer(String serverName) {
        return MySqlDB.getIns().getServerInformation(serverName);
    }

    public void setCanSave(boolean save) {
        this.canSave = save;
        if (!save) {
            this.serverInfo.setS(Status.OFFLINE);
        }
        Main.getInstance().getConfig().set("CanSave", save);
        try {
            Main.getInstance().getConfig().save(new File(Main.getInstance().getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void handleShutdown() {
        //Dirty update (sync)
        serverInfo.setS(Status.OFFLINE);
        serverInfo.setCurrentPlayers(0);

        if (canSave) {
            MySqlDB.getIns().updateServerInformation(serverInfo);
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        //UPDATE
        serverInfo.setCurrentPlayers(Bukkit.getServer().getOnlinePlayers().size());

        if (canSave) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                MySqlDB.getIns().updateServerInformation(serverInfo);
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        //UPDATE
        serverInfo.setCurrentPlayers(Bukkit.getServer().getOnlinePlayers().size() - 1);

        if (canSave) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                MySqlDB.getIns().updateServerInformation(serverInfo);
            });
        }
    }

    public void save() {
        if (canSave) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                MySqlDB.getIns().updateServerInformation(serverInfo);
            });
        }
    }

}
