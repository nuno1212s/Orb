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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    @Getter
    private Map<String, Class<?>> localClasses = new ConcurrentHashMap<>();

    public ModuleLoader(File moduleFile, GlobalClassLoader mainLoader) throws MalformedURLException {
        super(new URL[]{moduleFile.toURI().toURL()});
        this.moduleFile = moduleFile;
        this.globalLoader = mainLoader;
        this.globalLoader.addLoader(this);
    }

    public void load() {
        try (JarFile file = new JarFile(moduleFile)){

            System.out.println("LOADING MODULE: " + moduleFile.getName());

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

    public void setMainClass(Module j) {
        this.mainClass =  j;
        ModuleData annotation = j.getClass().getAnnotation(ModuleData.class);

        if (annotation == null) {
            System.out.println("Failed to load module data for module ");
            return;
        }

        // TODO: 04/10/2017 Remake the way the dependencies are reloaded because they need to load before the creation of the main class

        mainClass.setModuleName(annotation.name());
        mainClass.setVersion(annotation.version());
        mainClass.setDependencies(annotation.dependencies());
        this.mainClass.setInitLoader(this);
    }

    public void getMainClass(Config yml) {
        String mainClassPath = yml.getString("MainClass");

        try {
            Class toLoad = Class.forName(mainClassPath, false, this);
            Class<? extends Module> subClass;

            try {
                subClass = toLoad.asSubclass(Module.class);
            } catch (ClassCastException e) {
                System.out.println("The module's " + toLoad.getName() + " main class does not extend Module");
                return;
            }

            Module mainClass = subClass.newInstance();

            setMainClass(mainClass);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            System.out.println(mainClassPath);
            e.printStackTrace();
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return find(name, true);
    }

    public Class<?> find(String className, boolean global) throws ClassNotFoundException {

        //If the class is a bukkit or nms class, we don't need to search for it in the local plugin loaders
        if (className.startsWith("org.bukkit.") || className.startsWith("net.minecraft.")) {
            throw new ClassNotFoundException(className);
        }

        Class<?> result = this.localClasses.get(className);

        if (result == null) {
            if (global) {
                result = this.globalLoader.getClass(className);
            }

            if (result == null) {
                result = super.findClass(className);

                if (result != null){
                    globalLoader.setClass(className, result);
                }
            }

            localClasses.put(className, result);
        }

        return result;
    }

    public void shutdown() {
        try {
            this.close();
            this.globalLoader.removeLoader(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
