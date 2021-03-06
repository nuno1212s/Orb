package com.nuno1212s.npcinbox.main;

import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import com.nuno1212s.npcinbox.chat.ChatManager;
import com.nuno1212s.npcinbox.commands.RewardsCommand;
import com.nuno1212s.npcinbox.inventories.InventoryManager;
import com.nuno1212s.npcinbox.listeners.*;
import com.nuno1212s.npcinbox.npchandler.NPCManager;
import lombok.Getter;

/**
 * NPC inbox main class
 *
 * (Handles NPCS and inventories)
 */
@ModuleData(name = "NPCInbox", version = "1.0 BETA")
public class Main extends Module {

    @Getter
    private static Main ins;

    @Getter
    private ChatManager chatManager;

    @Getter
    private InventoryManager inventoryManager;

    @Getter
    private NPCManager npcManager;

    @Override
    public void onEnable() {
        ins = this;
        this.chatManager = new ChatManager();
        this.inventoryManager = new InventoryManager(this);
        this.npcManager = new NPCManager(this);

        MainData.getIns().getMessageManager().addMessageFile(getFile("messages.json", true));

        RewardsCommand commandExecutor = new RewardsCommand();
        registerCommand(new String[]{"reward"}, commandExecutor);
        registerCommand(new String[]{"inbox"}, commandExecutor);

        BukkitMain ins = BukkitMain.getIns();

        ins.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new ChatListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new InventoryListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new PlayerInteractEntityListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new PlayerRewardUpdateListener(), ins);
        ins.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), ins);

    }

    @Override
    public void onDisable() {
        this.npcManager.save();
    }
}
