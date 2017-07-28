package com.nuno1212s.multipliers.main;

import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import com.nuno1212s.multipliers.commands.ReloadRankMultipliersCommand;
import com.nuno1212s.multipliers.multipliers.RankManager;
import lombok.Getter;

/**
 * Main class
 */
@ModuleData(name = "Rank Multipliers", version = "1.0", dependencies = {})
public class RankMultiplierMain extends Module {

    @Getter
    static RankMultiplierMain ins;

    @Getter
    RankManager rankManager;

    @Override
    public void onEnable() {
        ins = this;
        rankManager = new RankManager(this);

        registerCommand(new String[]{"reloadrankmultipliers", "rrm"}, new ReloadRankMultipliersCommand());
    }

    @Override
    public void onDisable() {

    }
}
