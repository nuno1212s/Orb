package com.nuno1212s.enderchest.main;

import com.nuno1212s.enderchest.enderchestmanager.EnderChestManager;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;

@ModuleData(name = "EnderChest", version = "1.0")
public class Main extends Module {

    @Getter
    static Main ins;

    @Getter
    private EnderChestManager enderChestManager;

    @Override
    public void onEnable() {
        ins = this;
        enderChestManager = new EnderChestManager(this);
    }

    @Override
    public void onDisable() {

    }
}
