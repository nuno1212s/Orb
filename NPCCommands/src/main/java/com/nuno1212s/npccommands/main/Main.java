package com.nuno1212s.npccommands.main;

import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import com.nuno1212s.npccommands.commands.ListCommandsCommand;
import com.nuno1212s.npccommands.commands.RegisterCommand;
import com.nuno1212s.npccommands.commands.UnregisterCommand;
import com.nuno1212s.npccommands.listeners.PlayerInteractEntityListener;
import com.nuno1212s.npccommands.manager.NPCManager;
import lombok.Getter;

@ModuleData(name = "NPCCommands", version = "1.0")
public class Main extends Module {

    @Getter
    static Main ins;

    @Getter
    private NPCManager npcManager;

    @Override
    public void onEnable() {
        ins = this;
        npcManager = new NPCManager(this);

        BukkitMain ins = BukkitMain.getIns();

        ins.getServer().getPluginManager().registerEvents(new PlayerInteractEntityListener(), ins);

        registerCommand(new String[]{"registernpc"}, new RegisterCommand());
        registerCommand(new String[]{"unregisternpc"}, new UnregisterCommand());
        registerCommand(new String[]{"listcommands"}, new ListCommandsCommand());
    }

    @Override
    public void onDisable() {
        this.npcManager.saveNPCs();
    }
}
