package com.nuno1212s.punishments.main;

import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import com.nuno1212s.punishments.commands.PunishCommand;
import com.nuno1212s.punishments.commands.RemovePunishCommand;
import com.nuno1212s.punishments.inventories.InventoryManager;
import com.nuno1212s.punishments.listeners.InventoryClickListener;
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

        registerCommand(new String[]{"punish", "punir"}, new PunishCommand());
        registerCommand(new String[]{"unpunish", "despunir"}, new RemovePunishCommand());

        MainData.getIns().getMessageManager().addMessageFile(getFile("messages.json", true));

        MainData.getIns().getRedisHandler().registerRedisListener(new PunishmentsRedis());

        BukkitMain ins = BukkitMain.getIns();
        ins.getServer().getPluginManager().registerEvents(new InventoryClickListener(), ins);
    }

    @Override
    public void onDisable() {

    }
}
