package com.nuno1212s.warps.main;

import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import com.nuno1212s.warps.commands.ReloadWarpInventoryCommand;
import com.nuno1212s.warps.commands.WarpCommand;
import com.nuno1212s.warps.inventories.InventoryClickListener;
import com.nuno1212s.warps.inventories.InventoryManager;
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

    @Override
    public void onEnable() {
        ins = this;
        warpManager = new WarpManager(this);
        inventoryManager = new InventoryManager(this);

        WarpCommand cE = new WarpCommand();
        registerCommand(new String[]{"warp"}, cE);
        registerCommand(new String[]{"setwarp"}, cE);
        registerCommand(new String[]{"delwarp"}, cE);
        registerCommand(new String[]{"warps"}, cE);
        registerCommand(new String[]{"reloadwarpinventory", "rwi"}, new ReloadWarpInventoryCommand());

        MainData.getIns().getMessageManager().addMessageFile(getFile("messages.json", true));

        BukkitMain ins = BukkitMain.getIns();

        ins.getServer().getPluginManager().registerEvents(new InventoryClickListener(), ins);

    }

    @Override
    public void onDisable() {
        warpManager.save();
    }
}
