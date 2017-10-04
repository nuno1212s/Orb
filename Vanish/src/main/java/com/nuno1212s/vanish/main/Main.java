package com.nuno1212s.vanish.main;

import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import com.nuno1212s.vanish.commands.VanishCommand;
import com.nuno1212s.vanish.listener.PlayerDisconnectListener;
import com.nuno1212s.vanish.listener.PlayerJoinListener;
import com.nuno1212s.vanish.playermanager.PlayerManager;
import com.nuno1212s.vanish.redis.RedisHandler;
import com.nuno1212s.vanish.vanishmanager.VanishManager;
import lombok.Getter;

@ModuleData(name = "Vanish", version = "1.0-BETA", dependencies = {})
public class Main extends Module {

    @Getter
    private PlayerManager playerManager;

    @Getter
    private RedisHandler redisHandler;

    @Getter
    private VanishManager vanishManager;

    @Getter
    static Main ins;

    @Override
    public void onEnable() {
        ins = this;
        playerManager = new PlayerManager();
        redisHandler = new RedisHandler();
        vanishManager = new VanishManager();

        registerCommand(new String[]{"vanish", "v"}, new VanishCommand());

        BukkitMain ins = BukkitMain.getIns();
        ins.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new PlayerDisconnectListener(), ins);
    }

    @Override
    public void onDisable() {

    }
}
