package com.nuno1212s.modulemanager;

import lombok.Getter;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles module manager
 */
public class ModuleManager {

    private ArrayList<Module> modules;

    @Getter
    private File moduleFolder;

    private GlobalClassLoader loader;

    public ModuleManager(File dataFolder, ClassLoader classLoader) {

        loader = new GlobalClassLoader();
        modules = new ArrayList<>();
        moduleFolder = new File(dataFolder + File.separator + "Modules" + File.separator);

        if (!moduleFolder.exists()) {
            moduleFolder.mkdirs();
        }

        File[] files = moduleFolder.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.getName().endsWith(".jar")) {
                ModuleLoader moduleLoader = null;
                try {
                    moduleLoader = new ModuleLoader(file, classLoader, this.loader);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                moduleLoader.load();
                Module module = moduleLoader.getMainClass();
                modules.add(module);

                System.out.println("Loaded module: " + module.getModuleName());
                System.out.println("Module Version: " + module.getVersion());

            }
        }

        enable((ArrayList<Module>) this.modules.clone());
    }

    Module getModule(String name) {
        for (Module module : modules) {
            if (module.getModuleName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    private void enable(List<Module> modules) {

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
            /*
            We want to reverse the array because if plugin a depends on b,
            b needs to start first and shutdown after a.
             */
            Collections.reverse(moduleSorted);
            for (Module module : moduleSorted) {
                try {
                    module.onDisable();
                    module.setEnabled(false);
                    module.disable();
                    modules.remove(module);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            moduleSorted.clear();
        }

    }

    /**
     * Depth first sorting algorithm
     *
     * @param a          The module to solve
     * @param resolved   The list where all should be stored
     * @param unresolved Temp list.
     */
    private void dep_resolve(Module a, List<Module> resolved, List<Module> unresolved) {
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