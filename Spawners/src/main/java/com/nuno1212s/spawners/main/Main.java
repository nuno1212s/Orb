package com.nuno1212s.spawners.main;

import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import com.nuno1212s.spawners.commands.InstantRewardCommand;
import com.nuno1212s.spawners.commands.ReloadConfigCommand;
import com.nuno1212s.spawners.commands.SellCommand;
import com.nuno1212s.spawners.entitybundle.EntityBundleManager;
import com.nuno1212s.spawners.listeners.*;
import com.nuno1212s.spawners.playerdata.PlayerManager;
import com.nuno1212s.spawners.rewardhandler.RewardManager;
import lombok.Getter;

/**
 * Main
 */
@ModuleData(name = "Spawners", version = "1.0", dependencies = {"RankMultipliers"})
public class Main extends Module {

    @Getter
    static Main ins;

    @Getter
    private RewardManager rewardManager;

    @Getter
    private PlayerManager playerManager;

    @Getter
    private EntityBundleManager entityManager;

    @Override
    public void onEnable() {
        ins = this;

        rewardManager = new RewardManager(this);

        playerManager = new PlayerManager();

        entityManager = new EntityBundleManager(this);

        MainData.getIns().getMessageManager().addMessageFile(getFile("messages.json", true));

        registerCommand(new String[]{"vender"}, new SellCommand());
        registerCommand(new String[]{"instantreward"}, new InstantRewardCommand());
        registerCommand(new String[]{"reloadspawner"}, new ReloadConfigCommand());

        BukkitMain ins = BukkitMain.getIns();

        ins.getServer().getPluginManager().registerEvents(new PlayerKillMobListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new MobSpawnListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new ChunkUnloadListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new ChunkLoadListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new EntityDeathListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new SpawnerPlaceListener(), ins);

    }

    @Override
    public void onDisable() {
        rewardManager.saveConfig();
        entityManager.saveEntities();
    }
}
