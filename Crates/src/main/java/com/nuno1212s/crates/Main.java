package com.nuno1212s.crates;

import com.nuno1212s.crates.commands.CrateCommandManager;
import com.nuno1212s.crates.events.*;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Main Module
 */
@ModuleData(name = "Crates", version = "1.0-BETA", dependencies = {})
@Getter
public class Main extends Module {

    @Getter
    static Main ins;

    CrateManager crateManager;

    @Setter
    ServerEconomyInterface serverEconomyInterface;

    @Override
    public void onEnable() {
        ins = this;
        crateManager = new CrateManager(this);


        registerCommand(new String[]{"crate", "crates"}, new CrateCommandManager());

        MainData.getIns().getMessageManager().addMessageFile(getFile("messages.json", true));

        Plugin plugin = com.nuno1212s.main.Main.getIns();
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerCloseInventoryListener(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryClickEventListener(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteractListener(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerBreakBlockListener(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerChangeItemNameListener(), plugin);
    }

    @Override
    public void onDisable() {
        crateManager.save();
    }
}
