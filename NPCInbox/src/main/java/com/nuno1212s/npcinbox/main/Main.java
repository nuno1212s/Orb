package com.nuno1212s.npcinbox.main;

import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import com.nuno1212s.npcinbox.chat.ChatManager;
import com.nuno1212s.npcinbox.commands.RewardsCommand;
import com.nuno1212s.npcinbox.inventories.InventoryManager;
import com.nuno1212s.npcinbox.listeners.ChatListener;
import com.nuno1212s.npcinbox.listeners.InventoryListener;
import com.nuno1212s.npcinbox.listeners.PlayerQuitListener;
import lombok.Getter;

/**
 * NPC inbox main class
 *
 * (Handles NPCS and inventories)
 */
@ModuleData(name = "NPCInbox", version = "0.1 ALPHA", dependencies = {})
public class Main extends Module {

    @Getter
    private static Main ins;

    @Getter
    private ChatManager chatManager;

    @Getter
    private InventoryManager inventoryManager;

    @Override
    public void onEnable() {
        ins = this;
        this.chatManager = new ChatManager();
        this.inventoryManager = new InventoryManager(this);

        registerCommand(new String[]{"reward"}, new RewardsCommand());

        BukkitMain ins = BukkitMain.getIns();

        ins.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new ChatListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new InventoryListener(), ins);
    }

    @Override
    public void onDisable() {

    }
}
