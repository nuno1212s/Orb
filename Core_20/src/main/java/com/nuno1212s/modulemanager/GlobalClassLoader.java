package com.nuno1212s.modulemanager;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles all module class loaders
 */
public class GlobalClassLoader {

    List<URLClassLoader> loaders;

    Map<String, Class<?>> classes;

    public GlobalClassLoader() {
        loaders = new ArrayList<>();
        classes = new HashMap<>();
    }

    public void addLoader(ModuleLoader loader) {
        this.loaders.add(loader);
    }

    public void setClass(String name, Class<?> c) {
        this.classes.put(name, c);
    }

    public Class<?> getClass(String name) {
        return this.classes.get(name);
    }

}
