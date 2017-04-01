package com.nuno1212s.modulemanager;

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

    public ModuleManager(JavaPlugin p) {
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
            List<Module> moduleSorted = new ArrayList<>();
            dep_resolve(modules.get(0), moduleSorted, new ArrayList<>());
            for (Module module : moduleSorted) {
                module.onEnable();
                module.setEnabled(true);
                modules.remove(module);
            }
            moduleSorted.clear();
        }

    }

    public void disable() {
        disable((ArrayList<Module>) this.modules.clone());
    }

    private void disable(List<Module> modules) {

        while (!modules.isEmpty()) {
            List<Module> moduleSorted = new ArrayList<>();
            dep_resolve(modules.get(0), moduleSorted, new ArrayList<>());
            for (Module module : moduleSorted) {
                module.onDisable();
                module.setEnabled(false);
                modules.remove(module);
            }
            moduleSorted.clear();
        }

    }

    void dep_resolve(Module a, List<Module> resolved, List<Module> unresolved) {
        unresolved.add(a);
        for (Module m : a.getDependencies()) {
            if (resolved.contains(m)) {
                continue;
            }
            if (unresolved.contains(m)) {
                throw new IllegalArgumentException("Circular module dependency " + m.getModuleName());
            }
            if (m.isEnabled()) {
                continue;
            }
            dep_resolve(m, resolved, unresolved);
        }
        resolved.add(a);
        unresolved.remove(a);
    }


}