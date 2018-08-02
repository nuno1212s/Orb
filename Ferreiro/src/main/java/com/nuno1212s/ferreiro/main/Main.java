package com.nuno1212s.ferreiro.main;

import com.nuno1212s.ferreiro.commands.RepairCommand;
import com.nuno1212s.ferreiro.inventories.ConfirmInv;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;

import java.io.File;

/**
 * Main class
 */
@ModuleData(name = "Ferreiro", version = "0.1", dependencies = {})
public class Main extends Module {

    @Getter
    static Main ins;

    @Getter
    private ConfirmInv confirmInv;

    @Override
    public void onEnable() {
        // TODO: 01/09/2017 VERY UGLYYYYYY, remake ASAP

        ins = this;

        File f = new File(getDataFolder(), "confirmInv.json");

        if (!f.exists()) {
            saveResource(f, "confirmInv.json");
        }

        confirmInv = new ConfirmInv(f);

        registerCommand(new String[]{"repairitem", "repair"}, new RepairCommand());

        MainData.getIns().getMessageManager().addMessageFile(getFile("messages.json", true));

        BukkitMain ins = BukkitMain.getIns();


    }

    @Override
    public void onDisable() {

    }

}
