package com.nuno1212s.modulemanager;

import com.nuno1212s.main.Main;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Module class
 */
@Getter
public abstract class Module {

    @Setter
    String moduleName;

    @Setter
    String version;

    @Setter
    String[] dependencies;

    @Setter
    boolean enabled;

    public List<Module> getDependencies() {
        ArrayList<Module> modules = new ArrayList<>();
        ModuleManager ins = Main.getIns().getModuleManager();
        for (String dependency : this.dependencies) {
            Module module = ins.getModule(dependency);
            if (module == null) {
                continue;
            }
            modules.add(module);
        }
        return modules;
    }

    public abstract void onEnable(Plugin enabler);

    public abstract void onDisable();

}
