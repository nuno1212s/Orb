package com.nuno1212s.classes;

import com.nuno1212s.classes.classmanager.ClassManager;
import com.nuno1212s.classes.events.ClassEditInventoryListener;
import com.nuno1212s.classes.events.ClassInventoryClickListener;
import com.nuno1212s.modulemanager.Module;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

/**
 * Main
 */
public class Main extends Module {

    @Getter
    static Main ins;

    @Getter
    ClassManager classManager;

    @Override
    public void onEnable() {
        ins = this;
        classManager = new ClassManager(this);

        Plugin p = com.nuno1212s.main.Main.getIns();
        p.getServer().getPluginManager().registerEvents(new ClassEditInventoryListener(), p);
        p.getServer().getPluginManager().registerEvents(new ClassInventoryClickListener(), p);
    }

    @Override
    public void onDisable() {

    }
}
