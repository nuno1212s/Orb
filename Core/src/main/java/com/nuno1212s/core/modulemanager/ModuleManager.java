package com.nuno1212s.core.modulemanager;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles module manager
 */
public class ModuleManager {

    private ArrayList<Module> modules;

    File moduleFolder;

    @Getter
    static ModuleManager ins;

    public ModuleManager(JavaPlugin p) {
        ins = this;
        modules = new ArrayList<>();
        moduleFolder = new File(p.getDataFolder() + File.separator + "Modules" + File.separator);

        if (!moduleFolder.exists()) {
            moduleFolder.mkdirs();
        }

        File[] files = moduleFolder.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.getName().endsWith(".jar")) {
                ModuleLoader moduleLoader = new ModuleLoader(file);
                moduleLoader.load();
                Module module = moduleLoader.getMainClass();
                modules.add(module);

                System.out.println("Loaded module: " + module.getModuleName());
                System.out.println("Module Version: " + module.getVersion());

            }
        }

        enable((ArrayList<Module>) this.modules.clone());
    }

    public Module getModule(String name) {
        for (Module module : modules) {
            if (module.getModuleName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    public void enable(List<Module> modules) {

        while (!modules.isEmpty()) {
            dep_resolve(modules.get(0), new ArrayList<>());
            modules.removeIf(Module::isEnabled);
        }

    }

    void dep_resolve(Module a, List<Module> unresolved) {
        unresolved.add(a);
        for (Module m : a.getDependencies()) {
            if (m.isEnabled()) {
                continue;
            }
            if (unresolved.contains(m)) {
                throw new IllegalArgumentException("Circular module dependency " + m.getModuleName());
            }
            if (m.isEnabled()) {
                continue;
            }
            dep_resolve(m, unresolved);
        }
        a.setEnabled(true);
        unresolved.remove(a);
    }


}