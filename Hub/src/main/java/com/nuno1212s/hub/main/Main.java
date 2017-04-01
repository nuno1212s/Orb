package com.nuno1212s.hub.main;

import com.nuno1212s.core.util.PermissionServer;
import com.nuno1212s.hub.listeners.*;
import lombok.Getter;
import com.nuno1212s.hub.commands.ServerNpcCommand;
import com.nuno1212s.hub.commands.SpawnCommand;
import com.nuno1212s.hub.guis.InfoInventory;
import com.nuno1212s.hub.guis.OptionsInventory;
import com.nuno1212s.hub.guis.ProfileInventory;
import com.nuno1212s.hub.guis.ServerSelectorInventory;
import com.nuno1212s.hub.messagemanager.Messages;
import com.nuno1212s.hub.scoreboard.ScoreboardHandler;
import com.nuno1212s.hub.servermanager.ServerManager;
import com.nuno1212s.hub.utils.SpawnManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    public void onEnable() {
        instance = this;

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        new ServerManager(this);
        new ScoreboardHandler(this);
        new Messages(this);
        new SpawnManager(this);

        getCommand("servernpc").setExecutor(new ServerNpcCommand());
        SpawnCommand sc = new SpawnCommand();
        getCommand("spawn").setExecutor(sc);
        getCommand("setspawn").setExecutor(sc);

        getServer().getPluginManager().registerEvents(new NpcRightClick(this), this);
        getServer().getPluginManager().registerEvents(new HubListeners(this), this);
        getServer().getPluginManager().registerEvents(new PlayerChat(this), this);
        ServerSelectorInventory ssi = new ServerSelectorInventory(this);
        getServer().getPluginManager().registerEvents(ssi, this);
        getServer().getPluginManager().registerEvents(new ProfileInventory(this), this);
        getServer().getPluginManager().registerEvents(new OptionsInventory(this), this);
        getServer().getPluginManager().registerEvents(new InfoInventory(this), this);
        getServer().getPluginManager().registerEvents(new WeatherListener(), this);
        PlayerJoin listener = new PlayerJoin(this);
        //net.novusmc.core.main.Main.getInstance().registerEvent(listener);
        getServer().getPluginManager().registerEvents(listener, this);
        getServer().getPluginManager().registerEvents(new PlayerInteract(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDisconnectEvent(), this);

        //registerPermissions();

    }

    public void onDisable() {
        ServerManager.getIns().unload();
    }

    /*public void registerPermissions() {
        Main.getInstance().registerPermissions(new PermissionServer() {
            @Override
            public void setServerGroup(UUID player, short groupId) {

            }

            @Override
            public short getGroupId(UUID player) {
                return 0;
            }

            @Override
            public void handlePlayerGroupChange(UUID player) {
                PlayerData playerData = PlayerManager.getIns().getPlayerData(player);
                Player player1 = Bukkit.getPlayer(player);
                ScoreboardHandler.getIns().handlePlayerDC(player1, playerData);
                ScoreboardHandler.getIns().handlePlayerJoin(player1, playerData);
            }
        });
    }*/

}
