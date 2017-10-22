package com.nuno1212s.classes;

import com.nuno1212s.classes.classmanager.KitManager;
import com.nuno1212s.classes.commands.ClassCommandManager;
import com.nuno1212s.classes.commands.ClassesCommand;
import com.nuno1212s.classes.events.ClassEditInventoryListener;
import com.nuno1212s.classes.events.ClassDisplayInventoryClickListener;
import com.nuno1212s.classes.events.ClassesInventoryListener;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.modulemanager.ModuleData;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

/**
 * Main
 */
@ModuleData(name = "Classes", version = "1.0", dependencies = {})
public class Main extends Module {

    @Getter
    static Main ins;

    @Getter
    KitManager kitManager;

    @Override
    public void onEnable() {
        ins = this;
        kitManager = new KitManager(this);

        registerCommand(new String[]{"class"}, new ClassCommandManager());
        registerCommand(new String[]{"classes", "kits", "kit"}, new ClassesCommand());

        MainData.getIns().getMessageManager().addMessageFile(getFile("messages.json", true));

        Plugin p = BukkitMain.getIns();
        p.getServer().getPluginManager().registerEvents(new ClassEditInventoryListener(), p);
        p.getServer().getPluginManager().registerEvents(new ClassDisplayInventoryClickListener(), p);
        p.getServer().getPluginManager().registerEvents(new ClassesInventoryListener(), p);
    }

    @Override
    public void onDisable() {
        kitManager.save();
    }
}
