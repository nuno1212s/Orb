package com.nuno1212s.mercadonegro.main;

import com.nuno1212s.mercadonegro.inventories.InventoryManager;
import com.nuno1212s.modulemanager.Module;
import lombok.Getter;

/**
 * Main module class
 */
public class Main extends Module {

    @Getter
    private static Main ins;

    @Getter
    public InventoryManager inventoryManager;

    @Override
    public void onEnable() {
        ins = this;
        inventoryManager = new InventoryManager(this);
    }

    @Override
    public void onDisable() {

    }
}
