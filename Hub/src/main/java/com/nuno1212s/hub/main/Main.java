package com.nuno1212s.hub.main;

import com.nuno1212s.displays.DisplayMain;
import com.nuno1212s.hub.commands.RegisterNPCCommand;
import com.nuno1212s.hub.commands.UnregisterNPCCommand;
import com.nuno1212s.hub.hotbar.HotbarManager;
import com.nuno1212s.hub.listeners.*;
import com.nuno1212s.hub.mysql.MySql;
import com.nuno1212s.hub.npcs.NPCManager;
import com.nuno1212s.hub.player_options.PlayerOptionsManager;
import com.nuno1212s.hub.players_toggle.PlayerToggleManager;
import com.nuno1212s.hub.redis.RedisHandler;
import com.nuno1212s.hub.server_selector.GetPlayerCountCommand;
import com.nuno1212s.hub.server_selector.ServerSelectorManager;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;

/**
 * Main class file
 */
@ModuleData(name = "Hub", version = "1.0", dependencies = {"Displays", "NPCInbox"})
public class Main extends Module {

    @Getter
    static Main ins;

    @Getter
    private HotbarManager hotbarManager;

    @Getter
    private ServerSelectorManager serverSelectorManager;

    @Getter
    private PlayerOptionsManager playerOptionsManager;

    @Getter
    private RedisHandler redisHandler;

    @Getter
    private PlayerToggleManager playerToggleManager;

    @Getter
    private NPCManager npcManager;

    @Getter
    private MySql mySqlManager;

    @Override
    public void onEnable() {
        ins = this;
        mySqlManager = new MySql();
        hotbarManager = new HotbarManager(this);
        serverSelectorManager = new ServerSelectorManager(this);
        playerOptionsManager = new PlayerOptionsManager(this);
        playerToggleManager = new PlayerToggleManager(this);
        npcManager = new NPCManager(this);
        redisHandler = new RedisHandler();

        BukkitMain ins = BukkitMain.getIns();
        ins.getServer().getPluginManager().registerEvents(new PlayerInteractListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new PlayerInteractEntityListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new ServerInventoryClickListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new OptionsInventoryClickListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new PlayerDamageListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new PlayerDropItemListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new PlayerFoodChangeListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new WeatherChangeListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new PlayerAlterTerrainListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new CommandPreProcessListener(), ins);

        MainData.getIns().getMessageManager().addMessageFile(getFile("messages.json", true));

        registerCommand(new String[]{"getplayers"}, new GetPlayerCountCommand());
        registerCommand(new String[]{"registernpc"}, new RegisterNPCCommand());
        registerCommand(new String[]{"unregisternpc"}, new UnregisterNPCCommand());

        registerPlaceHolders();
    }

    @Override
    public void onDisable() {
        npcManager.save();
    }

    private void registerPlaceHolders() {
        DisplayMain.getIns().getPlaceHolderManager().registerPlaceHolder("%playerCount%", (d) ->
                String.valueOf(MainData.getIns().getServerManager().getTotalPlayerCount())
        );
    }
}
