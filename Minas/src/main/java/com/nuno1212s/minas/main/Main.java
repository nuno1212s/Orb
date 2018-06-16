package com.nuno1212s.minas.main;

import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.minas.commands.MineCommands;
import com.nuno1212s.minas.listeners.PlayerBreakBlockListener;
import com.nuno1212s.minas.minemanager.MineManager;
import com.nuno1212s.minas.timers.ResetTimers;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;

/**
 * Main module class
 */
@ModuleData(name = "Minas", version = "1.0", dependencies = {})
public class Main extends Module {

    @Getter
    static Main ins;

    @Getter
    private MineManager mineManager;

    @Override
    public void onEnable() {
        ins = this;
        mineManager = new MineManager(this);
        MainData.getIns().getScheduler().runTaskTimer(new ResetTimers(), 10, 1200);

        MainData.getIns().getMessageManager().addMessageFile(getFile("messages.json", true));

        BukkitMain ins = BukkitMain.getIns();
        ins.getServer().getPluginManager().registerEvents(new PlayerBreakBlockListener(), ins);

        registerCommand(new String[]{"mine"}, new MineCommands());
    }

    @Override
    public void onDisable() {
        mineManager.save();
    }
}
