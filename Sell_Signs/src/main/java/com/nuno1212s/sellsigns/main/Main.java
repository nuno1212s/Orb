package com.nuno1212s.sellsigns.main;

import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import com.nuno1212s.sellsigns.commands.ReloadRankMultCommands;
import com.nuno1212s.sellsigns.commands.ToggleEditModeCommand;
import com.nuno1212s.sellsigns.listeners.SignBreakListener;
import com.nuno1212s.sellsigns.listeners.SignClickListener;
import com.nuno1212s.sellsigns.listeners.SignPlaceListener;
import com.nuno1212s.sellsigns.rankmultipliers.RankMultipliers;
import com.nuno1212s.sellsigns.signs.SignManager;
import lombok.Getter;

/**
 * Main module class
 */
@ModuleData(name = "Sell signs", version = "0.1-ALPHA", dependencies = {})
public class Main extends Module {

    @Getter
    static Main ins;

    @Getter
    RankMultipliers rankMultipliers;

    @Getter
    SignManager signManager;

    @Override
    public void onEnable() {
        ins = this;
        signManager = new SignManager(this);
        rankMultipliers = new RankMultipliers(this);
        registerCommand(new String[]{"reloadranks"}, new ReloadRankMultCommands());
        registerCommand(new String[]{"editmode"}, new ToggleEditModeCommand());

        MainData.getIns().getMessageManager().addMessageFile(this.getFile("messages.json", true));

        BukkitMain ins = BukkitMain.getIns();

        ins.getServer().getPluginManager().registerEvents(new SignBreakListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new SignClickListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new SignPlaceListener(), ins);
    }

    public void reloadRankMult() {
        rankMultipliers = new RankMultipliers(this);
    }

    @Override
    public void onDisable() {
        signManager.saveSigns();
    }
}
