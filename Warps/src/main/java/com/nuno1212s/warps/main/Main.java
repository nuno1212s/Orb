package com.nuno1212s.warps.main;

import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import com.nuno1212s.warps.commands.*;
import com.nuno1212s.warps.filesystem.FileManager;
import com.nuno1212s.warps.homemanager.HomeManager;
import com.nuno1212s.warps.inventories.InventoryManager;
import com.nuno1212s.warps.listeners.PlayerJoinListener;
import com.nuno1212s.warps.listeners.PlayerMoveListener;
import com.nuno1212s.warps.listeners.PlayerQuitListener;
import com.nuno1212s.warps.timers.TeleportTimer;
import com.nuno1212s.warps.tpamanager.TPAManager;
import com.nuno1212s.warps.tpamanager.commands.TPAAcceptCommand;
import com.nuno1212s.warps.tpamanager.commands.TPACommand;
import com.nuno1212s.warps.tpamanager.commands.TPADenyCommand;
import com.nuno1212s.warps.warpmanager.WarpManager;
import lombok.Getter;

/**
 * Warp module
 */
@ModuleData(name = "Warps", version = "1.0", dependencies = {})
public class Main extends Module {

    @Getter
    static Main ins;

    @Getter
    private WarpManager warpManager;

    @Getter
    private InventoryManager inventoryManager;

    @Getter
    private HomeManager homeManager;

    @Getter
    private FileManager fileManager;

    @Getter
    private TeleportTimer teleportTimer;

    @Getter
    private TPAManager tpaManager;

    @Override
    public void onEnable() {
        ins = this;
        warpManager = new WarpManager(this);
        inventoryManager = new InventoryManager(this);
        fileManager = new FileManager(this);
        homeManager = new HomeManager();
        teleportTimer = new TeleportTimer();
        tpaManager = new TPAManager(this);

        WarpCommand cE = warpManager.getCommand();
        registerCommand(new String[]{"warp"}, cE);
        registerCommand(new String[]{"setwarp"}, cE);
        registerCommand(new String[]{"delwarp"}, cE);
        registerCommand(new String[]{"warps"}, cE);
        registerCommand(new String[]{"reloadwarpinventory", "rwi"}, new ReloadWarpInventoryCommand());
        registerCommand(new String[]{"sethome"}, new SetHomeCommand());
        registerCommand(new String[]{"delhome"}, new DelHomeCommand());
        registerCommand(new String[]{"home"}, new HomeCommand());
        registerCommand(new String[]{"tpa"}, new TPACommand());
        registerCommand(new String[]{"tpaaccept", "tpaccept"}, new TPAAcceptCommand());
        registerCommand(new String[]{"tpadeny"}, new TPADenyCommand());
        registerCommand(new String[]{"minas", "mina"}, new MinesCommand());

        MainData.getIns().getMessageManager().addMessageFile(getFile("messages.json", true));

        BukkitMain ins = BukkitMain.getIns();

        ins.getServer().getPluginManager().registerEvents(new PlayerMoveListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), ins);

    }

    @Override
    public void onDisable() {
        warpManager.save();
    }
}
