package com.nuno1212s.modulemanager;

import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Handles module manager
 */
public class ModuleManager {

    private ArrayList<Module> modules;

    @Getter
    private File moduleFolder;

    @Getter
    private GlobalClassLoader loader;

    public ModuleManager(File dataFolder, ClassLoader classLoader) {

        modules = new ArrayList<>();
        moduleFolder = new File(dataFolder + File.separator + "Modules" + File.separator);

        if (!moduleFolder.exists()) {
            moduleFolder.mkdirs();
        }

        File[] files = moduleFolder.listFiles();

        if (files == null) {
            return;
        }

        this.loader = new GlobalClassLoader(classLoader);

        for (File file : files) {
            if (file.getName().endsWith(".jar")) {
                ModuleLoader moduleLoader = null;
                try {
                    moduleLoader = new ModuleLoader(file, this.loader);

                    moduleLoader.load();
                    Module module = moduleLoader.getMainClass();
                    modules.add(module);

                    System.out.println("Loaded module: " + module.getModuleName());
                    System.out.println("Module Version: " + module.getVersion());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        enable((ArrayList<Module>) this.modules.clone());
    }

    /**
     * Get a module by name
     * @param name
     * @return
     */
    Module getModule(String name) {
        for (Module module : this.modules) {
            if (module.getModuleName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    /**
     * Enable the specified methods
     * Auto organizes the methods by their needed order (Dependencies)
     *
     * @param clonedModules
     */
    private void enable(List<Module> clonedModules) {

        while (!clonedModules.isEmpty()) {
            List<Module> moduleSorted = new ArrayList<>();

            dep_resolve(clonedModules.get(0), moduleSorted, new ArrayList<>());

            for (Module module : moduleSorted) {
                try {
                    System.out.println("ENABLING " + module.getModuleName());
                    module.onEnable();
                } catch (Exception e) {
                    System.out.println("Could not enable module " + module.getModuleName());
                    e.printStackTrace();
                } finally {
                    module.setEnabled(true);
                    clonedModules.remove(module);
                }
            }

            moduleSorted.clear();
        }

    }

    public void disable() {
        disable((ArrayList<Module>) this.modules.clone());
    }

    private void disable(List<Module> clonedModules) {

        while (!clonedModules.isEmpty()) {
            List<Module> moduleSorted = new ArrayList<>();
            dep_resolve(clonedModules.get(0), moduleSorted, new ArrayList<>());
            /*
            We want to reverse the array because if plugin a depends on b,
            b needs to start first and shutdown after a.
             */
            Collections.reverse(moduleSorted);
            for (Module module : moduleSorted) {
                try {
                    module.onDisable();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    clonedModules.remove(module);
                    module.setEnabled(false);
                    module.disable();
                }
            }
            moduleSorted.clear();
        }

    }

    /**
     * Depth first sorting algorithm
     *
     * @param a          The module to solve
     * @param resolved   The list where all the sorted modules should be stored
     * @param unresolved Temp list.
     */
    private void dep_resolve(Module a, List<Module> resolved, List<Module> unresolved) {
        unresolved.add(a);
        System.out.println("RESOLVING " + a.getModuleName());

        for (Module m : a.getDependencies(this)) {
            System.out.println("DEPENDENCY " + m.getModuleName());
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