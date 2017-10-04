package com.nuno1212s.modulemanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles all module class loaders
 */
public class
GlobalClassLoader {

    private List<ModuleLoader> loaders;

    private Map<String, Class<?>> classes;

    private ClassLoader bukkitLoader;

    public GlobalClassLoader(ClassLoader bukkitLoader) {
        this.bukkitLoader = bukkitLoader;
        loaders = new ArrayList<>();
        classes = new HashMap<>();
    }

    public void addLoader(ModuleLoader loader) {
        this.loaders.add(loader);
    }

    public void removeLoader(ModuleLoader loader) {

        /**
         * Remove all the classes from that module from the classes saved in the class loader
         */

        loader.getLocalClasses().keySet().forEach(classes::remove);

        this.loaders.remove(loader);
    }

    public void setClass(String name, Class<?> c) {
        this.classes.put(name, c);
    }

    public Class<?> getClass(String name/*, ModuleLoader origin*/) {
        if (this.classes.containsKey(name)) {
            return this.classes.get(name);
        }

        Class<?> Class = null;

        for (ModuleLoader loader : loaders) {

            /*if (loader == origin) {
                //This module has already been scanned it would be a waste of performance to scan it again
                continue;
            }*/

            try {
                Class = loader.find(name, false);
            } catch (ClassNotFoundException e) {}

            if (Class != null) {
                return Class;
            }
        }

        try {
            Class = bukkitLoader.loadClass(name);
        } catch (ClassNotFoundException e) {}

        return Class;
    }

}
