package com.nuno1212s.mercadonegro.main;

import com.nuno1212s.main.MainData;
import com.nuno1212s.mercadonegro.commands.MarketOpenCommand;
import com.nuno1212s.mercadonegro.inventories.InventoryManager;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;

/**
 * Main module class
 */
@ModuleData(name = "Mercado Negro", version = "1.0", dependencies = {})
public class Main extends Module {

    @Getter
    private static Main ins;

    @Getter
    public InventoryManager inventoryManager;

    @Override
    public void onEnable() {
        ins = this;
        inventoryManager = new InventoryManager(this);

        registerCommand(new String[]{"blackmarket", "mercadonegro"}, new MarketOpenCommand());

        MainData.getIns().getMessageManager().addMessageFile(getFile("messages.json", true));
    }

    @Override
    public void onDisable() {
    }
}
