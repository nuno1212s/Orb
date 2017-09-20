package com.nuno1212s.punishments.main;

import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import com.nuno1212s.punishments.commands.TestCommand;
import com.nuno1212s.punishments.inventories.InventoryManager;
import com.nuno1212s.punishments.redis.PunishmentsRedis;
import lombok.Getter;

@ModuleData(name = "Punishments", version = "1.0")
public class Main extends Module {

    @Getter
    static Main ins;

    @Getter
    private InventoryManager inventoryManager;

    @Override
    public void onEnable() {
        ins = this;
        inventoryManager = new InventoryManager(this);

        registerCommand(new String[]{"testpunish"}, new TestCommand());

        MainData.getIns().getRedisHandler().registerRedisListener(new PunishmentsRedis());
    }

    @Override
    public void onDisable() {

    }
}
