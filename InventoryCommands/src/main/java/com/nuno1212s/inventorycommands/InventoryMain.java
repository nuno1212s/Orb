package com.nuno1212s.inventorycommands;

import com.nuno1212s.inventorycommands.commands.CmdExecutor;
import com.nuno1212s.inventorycommands.commands.CommandManager;
import com.nuno1212s.inventorycommands.commands.ReloadInventoriesCommand;
import com.nuno1212s.inventorycommands.inventories.InventoryManager;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;

@ModuleData(name = "Inventory_Commands", version = "1.0-BETA")
public class InventoryMain extends Module {

    @Getter
    static InventoryMain ins;

    @Getter
    CommandManager commandManager;

    @Getter
    InventoryManager inventoryManager;

    private CmdExecutor commandExecutor;

    @Override
    public void onEnable() {

        ins = this;

        commandManager = new CommandManager(this);

        inventoryManager = new InventoryManager(this);

        this.commandExecutor = new CmdExecutor();

        registerCommand(new String[]{"reloadinventories"}, new ReloadInventoriesCommand());

    }

    @Override
    public void onDisable() {

    }

    public void registerCommand(String commandName) {
        registerCommand(new String[]{commandName}, this.commandExecutor);
    }
}
