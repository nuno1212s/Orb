package com.nuno1212s.mercadonegro.main;

import com.nuno1212s.main.MainData;
import com.nuno1212s.mercadonegro.commands.MarketCommandManager;
import com.nuno1212s.mercadonegro.economy.ServerEconomyHandler;
import com.nuno1212s.mercadonegro.events.InventoryClickListener;
import com.nuno1212s.mercadonegro.inventories.InventoryManager;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;

/**
 * Main module class
 */
@ModuleData(name = "Mercado Negro", version = "1.0", dependencies = {})
public class Main extends Module {

    @Getter
    private static Main ins;

    @Getter
    public InventoryManager inventoryManager;

    @Getter
    @Setter
    ServerEconomyHandler economyHandler;

    @Override
    public void onEnable() {
        ins = this;
        inventoryManager = new InventoryManager(this);

        MainData.getIns().getMessageManager().addMessageFile(getFile("messages.json", true));

        registerCommand(new String[]{"market"}, new MarketCommandManager());

        Plugin p = com.nuno1212s.main.Main.getIns();
        p.getServer().getPluginManager().registerEvents(new InventoryClickListener(), p);
    }

    @Override
    public void onDisable() {
        inventoryManager.save();
    }
}
