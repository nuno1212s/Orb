package com.nuno1212s.ferreiro.main;

import com.nuno1212s.ferreiro.commands.RepairCommand;
import com.nuno1212s.ferreiro.inventories.ConfirmInventory;
import com.nuno1212s.ferreiro.listeners.ConfirmInventoryListener;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Main class
 */
@ModuleData(name = "Ferreiro", version = "0.1", dependencies = {})
public class Main extends Module {

    @Getter
    static Main ins;

    private Map<UUID, ConfirmInventory> inventories;

    @Override
    public void onEnable() {
        // TODO: 01/09/2017 VERY UGLYYYYYY, remake ASAP

        ins = this;
        inventories = new HashMap<>();

        registerCommand(new String[]{"repairitem", "repair"}, new RepairCommand());

        MainData.getIns().getMessageManager().addMessageFile(getFile("messages.json", true));

        BukkitMain ins = BukkitMain.getIns();

        ins.getServer().getPluginManager().registerEvents(new ConfirmInventoryListener(), ins);

    }

    @Override
    public void onDisable() {

    }

    public void addInventory(UUID player, ConfirmInventory c) {
        this.inventories.put(player, c);
    }

    public ConfirmInventory getInventory(UUID player) {
        return this.inventories.get(player);
    }

    public void removeInventory(UUID player) {
        this.inventories.remove(player);
    }

}
