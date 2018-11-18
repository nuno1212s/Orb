package com.nuno1212s.duels;

import com.nuno1212s.duels.arenas.ArenaManager;
import com.nuno1212s.duels.duelmanager.DuelManager;
import com.nuno1212s.duels.matchmaking.MatchmakingManager;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;


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

    }

    @Override
    public void onDisable() {

    }
}
