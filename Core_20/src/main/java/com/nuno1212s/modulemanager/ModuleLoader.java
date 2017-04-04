package com.nuno1212s.modulemanager;

import com.nuno1212s.config.BukkitConfig;
import com.nuno1212s.config.BungeeConfig;
import com.nuno1212s.config.Config;
import com.nuno1212s.main.MainData;
import lombok.Cleanup;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Loads a module
 */
public class ModuleLoader extends URLClassLoader {

    File moduleFile;

    @Getter
    Module mainClass;

    private GlobalClassLoader globalLoader;

    public ModuleLoader(File moduleFile, ClassLoader loader, GlobalClassLoader mainLoader) throws MalformedURLException {
        super(new URL[]{moduleFile.toURI().toURL()}, loader);
        this.moduleFile = moduleFile;
        this.globalLoader = mainLoader;
    }

    public void load() {
        try (JarFile file = new JarFile(moduleFile)){
            ZipEntry entry = file.getEntry("moduleInfo.yml");
            if (entry == null) {
                System.out.println("Module Info is missing from module " + moduleFile.getName().replace(".jar", ""));
                return;
            }
            @Cleanup
            InputStream stream = file.getInputStream(entry);
            if (MainData.getIns().isBungee()) {
                getMainClass(new BungeeConfig(stream));
            } else {
                getMainClass(new BukkitConfig(stream));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMainClass(Object j) {
        this.mainClass = (Module) j;
        ModuleData annotation = j.getClass().getAnnotation(ModuleData.class);
        mainClass.setModuleName(annotation.name());
        mainClass.setVersion(annotation.version());
        mainClass.setDependencies(annotation.dependencies());
    }

    public void getMainClass(Config yml) {
        String mainClassPath = yml.getString("MainClass");

        try {
            Class toLoad = Class.forName(mainClassPath, true, this);
            Object mainClass = toLoad.newInstance();
            setMainClass(mainClass);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return find(name);
    }

    private Class<?> find(String className) {

        Class<?> result = null;

        result = this.globalLoader.getClass(className);

        if (result != null) {
            return result;
        }

        try {
            result = super.findClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (result != null) {
            this.globalLoader.setClass(className, result);
        }

        return result;
    }

    public void shutdown() {
        try {
            this.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
