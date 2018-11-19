package com.nuno1212s.duels;

import com.nuno1212s.duels.arenas.ArenaManager;
import com.nuno1212s.duels.duelmanager.DuelManager;
import com.nuno1212s.duels.listeners.EntityDamageListener;
import com.nuno1212s.duels.matchmaking.MatchmakingManager;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;
import org.bukkit.Bukkit;


@ModuleData(name = "", version = "")
public class DuelMain extends Module  {

    @Getter
    private static DuelMain ins;

    @Getter
    private ArenaManager arenaManager;

    @Getter
    private DuelManager duelManager;

    @Getter
    private MatchmakingManager matchmakingManager;

    @Override
    public void onEnable() {

        ins = this;

        arenaManager = new ArenaManager(this);
        duelManager = new DuelManager();
        matchmakingManager = new MatchmakingManager();

        Bukkit.getServer().getPluginManager().registerEvents(new EntityDamageListener(), BukkitMain.getIns());

    }

    @Override
    public void onDisable() {

    }
}
